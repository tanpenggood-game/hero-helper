package com.itplh.hero.service;

import com.itplh.hero.domain.Action;
import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.core.NPCFixedEvent;
import com.itplh.hero.util.MoveUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.itplh.hero.util.CollectionUtil.getLog;
import static com.itplh.hero.util.ElementUtil.queryURIByLinkName;
import static com.itplh.hero.util.GameUtil.requestAutoBattle;
import static com.itplh.hero.util.GameUtil.requestReturnGameMainPage;
import static com.itplh.hero.util.Go1000mUtil.go1000m;
import static com.itplh.hero.util.RequestUtil.requestByLinkName;
import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

@Slf4j
@Service
public class DefaultEventHandleService implements EventHandleService {

    @Override
    public Optional<Document> handle(AbstractEvent event, OperationResource operationResource) {
        // check is waiting for resource refresh
        if (operationResource.isWaitingForResourceRefresh()) {
            waitingResourceLogIfNecessary(event, operationResource);
            return Optional.empty();
        }
        // set last run time
        operationResource.setLastRunTime(LocalDateTime.now());

        // 1. request uri
        Document document = sleepThenGETRequest(event.eventContext().buildURI(),
                "start " + operationResource.getOperateName());
        // 2. revise to target position
        document = go1000m(document, operationResource.getStartPosition()).orElse(null);
        // 3. execute action
        boolean isFixedResource = operationResource.isFixedResource();
        List<String> globalOperationObjects = operationResource.getGlobalOperationObjects();
        for (Action action : operationResource.getActions()) {
            // request move 1 step, if necessary
            if (Optional.ofNullable(action.getDirection()).isPresent()) {
                document = MoveUtil.move(document, action.getDirection()).orElse(null);
            }
            // execute action callback
            document = executeActionCallback(document, action, globalOperationObjects, isFixedResource);
            // return empty, if document is null
            if (Objects.isNull(document)) {
                String sid = event.eventContext().getSid();
                String eventName = event.eventContext().getEventName();
                log.warn("terminate operation [document=null] [sid={}] [eventName={}] [operateName={}] [action={}]",
                        sid, eventName, operationResource.getOperateName(), action);
                return Optional.empty();
            }
        }
        // 4. return game main page
        return requestReturnGameMainPage(document);
    }

    @Override
    public boolean handle(AbstractEvent event, Map<String, OperationResource> operationResourceMap) {
        if (CollectionUtils.isEmpty(operationResourceMap)) {
            return true;
        }
        // select operable resources
        Collection<OperationResource> operableResources = selectOperableResources(event, operationResourceMap);
        // execute operate
        boolean unfixedResource = !Objects.equals(event.eventContext().getEventName(), NPCFixedEvent.class.getSimpleName());
        List<Boolean> successCounter = operableResources.stream()
                .map(operationResource -> {
                    String sid = event.eventContext().getSid();
                    String eventName = event.eventContext().getEventName();
                    String operateName = operationResource.getOperateName();
                    if (unfixedResource) {
                        log.debug("start [sid={}] [event={}] [operation={}]", sid, eventName, operateName);
                    }
                    boolean isSuccess = handle(event, operationResource).isPresent();
                    if (unfixedResource) {
                        log.debug("finish [sid={}] [event={}] [operation={}] [isSuccess={}]", sid, eventName, operateName, isSuccess);
                    }
                    return isSuccess;
                })
                .filter(Boolean::booleanValue)
                .collect(Collectors.toList());
        return successCounter.size() == operableResources.size();
    }

    private Document executeActionCallback(Document document,
                                           Action action,
                                           Collection<String> globalOperationObjects,
                                           boolean isFixedResource) {
        if (!action.isHasCallback() || Objects.isNull(document)) {
            return document;
        }
        // if first is battle page
        document = requestAutoBattle(document).orElse(null);
        // action handle, includes operation objects and steps
        Set<String> operationObjects = new HashSet<>();
        operationObjects.addAll(globalOperationObjects);
        operationObjects.addAll(action.getOperationObjects());
        return actionHandler(document, action, operationObjects, isFixedResource);
    }

    private Document actionHandler(Document document,
                                   Action action,
                                   Collection<String> operationObjects,
                                   boolean isFixedResource) {
        while (queryURIByLinkName(document, operationObjects).isPresent()) {
            // request operation object
            document = requestByLinkName(document, operationObjects, getLog(operationObjects)).orElse(null);
            // operation steps, if exists
            for (String step : action.getOperationSteps()) {
                if (queryURIByLinkName(document, step).isPresent()) {
                    document = requestByLinkName(document, step, getLog(Arrays.asList(step))).orElse(null);
                }
            }
            // attack operation object, if necessary
            document = requestAutoBattle(document).orElse(null);
            // only operation once and return game main page, if fixed resource
            if (isFixedResource) {
                document = requestReturnGameMainPage(document).orElse(null);
                break;
            }
        }
        return document;
    }

    /**
     * select operable resources
     * <p>
     * extendInfo includes parameter as follow:
     * 1. mapping, used to specify the operation object
     * 2. novice, used to identify novices
     *
     * @param event
     * @param operationResourceMap
     * @return
     */
    private Collection<OperationResource> selectOperableResources(AbstractEvent event, Map<String, OperationResource> operationResourceMap) {
        Collection<OperationResource> operationResourceList;
        Map<String, String> extendInfo = event.eventContext().getExtendInfo();
        Optional<String> mapping = Optional.ofNullable(extendInfo.get("mapping"));
        Optional<String> novice = Optional.ofNullable(extendInfo.get("novice"));
        if (mapping.isPresent()) {
            // select user input part
            operationResourceList = Arrays.asList(mapping.get().split(","))
                    .stream()
                    .filter(e -> !StringUtils.isEmpty(e))
                    .map(e -> operationResourceMap.get(e))
                    // exclude null
                    .filter(resource -> Optional.ofNullable(resource).isPresent())
                    .collect(Collectors.toList());
        } else {
            // auto select all
            operationResourceList = operationResourceMap.values();
        }

        // only select first, if event equals npc fixed event
        if (event instanceof NPCFixedEvent) {
            operationResourceList = operationResourceList.stream()
                    .findFirst()
                    .map(Arrays::asList)
                    .orElse(Collections.EMPTY_LIST);
        }

        boolean isNovice = novice.map(Boolean::valueOf).orElse(false);
        return operationResourceList.stream()
                // exclude be protected resource
                .filter(operationResource -> operationResource.isUnprotected(isNovice))
                // exclude waiting for refresh
                .filter(operationResource -> {
                    waitingResourceLogIfNecessary(event, operationResource);
                    return !operationResource.isWaitingForResourceRefresh();
                })
                // sort by priority desc
                .sorted(Comparator.comparing(OperationResource::getPriority).reversed())
                .collect(Collectors.toList());
    }

    private void waitingResourceLogIfNecessary(AbstractEvent event, OperationResource operationResource) {
        if (operationResource.isWaitingForResourceRefresh()) {
            String sid = event.eventContext().getSid();
            String eventName = event.eventContext().getEventName();
            String operateName = operationResource.getOperateName();
            long refreshFrequency = operationResource.getRefreshFrequency();
            LocalDateTime lastRunTime = operationResource.getLastRunTime();
            log.debug("waiting for resource refresh [sid={}] [event={}] [operation={}] [last run time={}] [refresh frequency={}] ",
                    sid, eventName, operateName, lastRunTime, refreshFrequency);
        }
    }

}
