package com.itplh.hero.service;

import com.itplh.hero.constant.ParameterEnum;
import com.itplh.hero.domain.Action;
import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;
import com.itplh.hero.event.core.NPCFixedEvent;
import com.itplh.hero.listener.EventBus;
import com.itplh.hero.util.CollectionUtil;
import com.itplh.hero.util.GameUtil;
import com.itplh.hero.util.MoveUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    @Autowired
    private EventBus eventBus;

    @Override
    public boolean handle(AbstractEvent event, Collection<OperationResource> executableResources) {
        if (Objects.isNull(event) || CollectionUtils.isEmpty(executableResources)) {
            return false;
        }
        // execute operate
        boolean isNotFixedResource = !Objects.equals(event.eventContext().getEventName(), NPCFixedEvent.class.getSimpleName());
        List<Boolean> successCounter = executableResources.stream()
                .map(operableResource -> {
                    String sid = event.eventContext().getSid();
                    String eventName = event.eventContext().getEventName();
                    String operateName = operableResource.getOperateName();
                    long start = System.currentTimeMillis();
                    if (isNotFixedResource) {
                        log.debug("start [sid={}] [event={}] [operation={}]", sid, eventName, operateName);
                    }
                    boolean isSuccess = handle(event, operableResource).isPresent();
                    if (isNotFixedResource) {
                        long costSeconds = (System.currentTimeMillis() - start) / 1000;
                        log.debug("finish [sid={}] [event={}] [operation={}] [costSeconds={}] [isSuccess={}]",
                                sid, eventName, operateName, costSeconds, isSuccess);
                    }
                    return isSuccess;
                })
                .filter(Boolean::booleanValue)
                .collect(Collectors.toList());
        return successCounter.size() == executableResources.size();
    }

    private Optional<Document> handle(AbstractEvent event, OperationResource executableResource) {
        String sid = event.eventContext().getSid();
        String eventName = event.eventContext().getEventName();
        String operateName = executableResource.getOperateName();
        // if this event already closed
        if (eventBus.isClosedEvent(sid)) {
            log.info("event is closed [sid={}] [eventName={}] [operateName={}]", sid, eventName, operateName);
            return Optional.empty();
        }
        // if this event already pause
        if (eventBus.isPauseEvent(sid)) {
            log.info("event is paused [sid={}] [eventName={}] [operateName={}]", sid, eventName, operateName);
            return Optional.empty();
        }
        // set start run time
        executableResource.setLastRunTime(LocalDateTime.now());
        // 1. request uri
        Document document = sleepThenGETRequest(event.eventContext().buildURI(),
                "start " + executableResource.getOperateName());
        // 2. revise to target position
        document = go1000m(document, executableResource.getStartPosition()).orElse(null);
        // supply grain if necessary
        document = supplyGrainIfNecessary(event, document);
        // set run time, second times ensure start run time more accuracy
        executableResource.setLastRunTime(LocalDateTime.now());
        // 3. execute action
        document = executeActions(document, event, executableResource);
        // 4. return game main page
        return requestReturnGameMainPage(document);
    }

    private Document executeActions(Document document,
                                    AbstractEvent event,
                                    OperationResource executableResource) {
        String sid = event.eventContext().getSid();
        String eventName = event.eventContext().getEventName();
        String operateName = executableResource.getOperateName();
        for (Action action : executableResource.allActions()) {
            // if this event already close
            if (eventBus.isClosedEvent(sid)) {
                log.info("event is closed [sid={}] [eventName={}] [operateName={}] [action={}]", sid, eventName, operateName, action);
                break;
            }
            // if this event already pause
            if (eventBus.isPauseEvent(sid)) {
                log.info("event is paused [sid={}] [eventName={}] [operateName={}] [action={}]", sid, eventName, operateName, action);
                break;
            }
            // request move 1 step, if necessary
            if (Optional.ofNullable(action.getDirection()).isPresent()) {
                document = MoveUtil.move(document, action.getDirection()).orElse(null);
            }
            // execute action callback
            document = executeActionCallback(document, action, executableResource, event);
            // return empty, if document is null
            if (Objects.isNull(document)) {
                log.warn("terminate [document=null] [sid={}] [eventName={}] [operateName={}] [action={}]",
                        sid, eventName, operateName, action);
                break;
            }
        }
        return document;
    }

    private Document executeActionCallback(Document document,
                                           Action action,
                                           OperationResource executableResource,
                                           AbstractEvent event) {
        if (!action.isHasCallback() || Objects.isNull(document)) {
            return document;
        }
        // if first is battle page
        if (GameUtil.isBattlePage(document)) {
            document = requestAutoBattle(document).orElse(null);
        }
        // action handle, includes operation objects and steps
        return actionCallbackHandler(document, action, executableResource, event);
    }

    private Document actionCallbackHandler(Document document,
                                           Action action,
                                           OperationResource executableResource,
                                           AbstractEvent event) {
        Collection<String> operationObjects = CollectionUtil.merge(executableResource.getGlobalOperationObjects(), action.getOperationObjects());
        Collection<String> operationSteps = CollectionUtil.merge(executableResource.getGlobalOperationSteps(), action.getOperationSteps());
        while (queryURIByLinkName(document, operationObjects).isPresent()) {
            // request operation object
            document = requestByLinkName(document, operationObjects, getLog(operationObjects)).orElse(null);
            int operateTimes = 0;
            // operation steps, if exists
            for (String step : operationSteps) {
                if (queryURIByLinkName(document, step).isPresent()) {
                    ++operateTimes;
                    document = requestByLinkName(document, step, getLog(Arrays.asList(step))).orElse(null);
                }
            }
            // attack operation object, if necessary
            if (GameUtil.isBattlePage(document)) {
                ++operateTimes;
                document = requestAutoBattle(document).orElse(null);
            }
            // break loop, if operate 0 times
            if (operateTimes == 0) {
                HeroEventContext eventContext = event.eventContext();
                log.warn("operate 0 times [sid={}] [event={}] [operation={}] [operationObjects={}]",
                        eventContext.getSid(), eventContext.getEventName(), executableResource.getOperateName(), operationObjects);
                break;
            }
            // only operate once and break loop, if fixed resource
            if (executableResource.isFixedResource()) {
                document = requestReturnGameMainPage(document).orElse(null);
                break;
            }
        }
        return document;
    }

    private Document supplyGrainIfNecessary(AbstractEvent event, Document document) {
        boolean isSupplyGrain = event.eventContext().queryExtendInfo(ParameterEnum.SUPPLY_GRAIN)
                .map(Boolean::valueOf)
                .orElse(false);
        if (isSupplyGrain) {
            document = GameUtil.requestSupplyGrain(document).orElse(null);
        }
        return document;
    }

}
