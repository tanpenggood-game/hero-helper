package com.itplh.hero.listener;

import com.itplh.hero.constant.ParameterEnum;
import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;
import com.itplh.hero.event.core.NPCFixedEvent;
import com.itplh.hero.event.core.NPCTimedRefreshEvent;
import com.itplh.hero.util.EventTemplateUtil;
import com.itplh.hero.util.SnapshotUtil;
import com.itplh.hero.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultApplicationListenerHelper implements ApplicationListenerHelper {

    @Autowired
    private EventBus eventBus;

    @Override
    public void doOnApplicationEvent(AbstractEvent event,
                                     Function<Collection<OperationResource>, Boolean> doBusiness) {
        HeroEventContext heroEventContext = event.eventContext();
        String sid = heroEventContext.getUser().getSid();
        if (!eventBus.containsEvent(sid)) {
            return;
        }

        // 从Spring的单例实例中深拷贝数据，保证不同用户触发的事件数据隔离，并且不对单例对象中的模版数据产生影响
        Map<String, OperationResource> eventResourceTemplate = EventTemplateUtil.copyOperationResourceTemplate(event.getClass());
        // computed operable resource
        Map<String, OperationResource> operableResources = getOperableResources(event, eventResourceTemplate);
        // replay snapshot
        SnapshotUtil.replaySnapshot(event, operableResources);

        String eventName = heroEventContext.getEventName();
        log.info("start [event={}] [sid={}]", eventName, sid);

        long targetRunRound = heroEventContext.getTargetRunRound();
        long successRunRound = heroEventContext.getSuccessRunRound();
        long actualRunRound = heroEventContext.getActualRunRound();
        while (eventBus.containsEvent(sid)) {
            ++actualRunRound;
            // pause
            if (eventBus.isPauseEvent(sid)) {
                // save snapshot
                SnapshotUtil.saveSnapshot(event, eventResourceTemplate);
                break;
            }
            // crate new event or restart event
            boolean isEnd = actualRunRound > targetRunRound && targetRunRound != -1;
            if (isEnd) {
                eventBus.close(sid);
                break;
            }
            // handle event
            long start = System.currentTimeMillis();
            try {
                // each round real time computing executable resource
                Collection<OperationResource> executableResources = getExecutableResources(event, operableResources.values());
                printLogForNextExecuteResource(event, executableResources);
                // handle these executable resources
                boolean isSuccess = doBusiness.apply(executableResources);
                if (isSuccess) {
                    // real time update success run round
                    event.eventContext().setSuccessRunRound(++successRunRound);
                }
            } catch (Throwable e) {
                log.warn("No.{} round [event={}] exception, [sid={}] =======================", actualRunRound, eventName, sid);
                log.error(e.getMessage(), e);
            } finally {
                // real time update actual run round
                event.eventContext().setActualRunRound(actualRunRound);
                // print log, and sleep the right time
                logAndSleepIfNecessary(event, operableResources, start);
            }
        }

        log.info("finish [event={}] [sid={}]", eventName, sid);
    }

    private void logAndSleepIfNecessary(AbstractEvent event,
                                        Map<String, OperationResource> operableResources,
                                        long start) {
        try {
            long costSeconds = (System.currentTimeMillis() - start) / 1000;
            String sid = event.eventContext().getUser().getSid();
            String eventName = event.eventContext().getEventName();
            long actualRunRound = event.eventContext().getActualRunRound();
            boolean isEnterSleep = isEnterSleep(event, operableResources);
            long sleepSeconds = isEnterSleep ? realTimeComputingSleepSeconds(event, operableResources) : 0L;
            log.info("No.{} round [costSeconds={}] [event={}] finish, [sid={}] [sleepSeconds={}] [isEnterSleep={}]",
                    actualRunRound, costSeconds, eventName, sid, sleepSeconds, isEnterSleep);

            while (isEnterSleep(event, operableResources)) {
                ThreadUtil.sleep(3, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            // ignore
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 返回用户可操作的资源
     * <p>
     * 通过静态配置项和用户输入参数，将会过滤掉一些资源
     * 过滤条件更静态，只需要计算一次
     *
     * @param event
     * @param operationResourceTemplate
     * @return
     */
    private Map<String, OperationResource> getOperableResources(AbstractEvent event,
                                                                Map<String, OperationResource> operationResourceTemplate) {
        // parse user input parameters
        List resourceParameters = event.eventContext()
                .queryExtendInfo(ParameterEnum.RESOURCE)
                .map(e -> e.split(","))
                .map(Arrays::asList)
                .orElse(Collections.EMPTY_LIST);
        boolean isNovice = event.eventContext()
                .queryExtendInfo(ParameterEnum.NOVICE)
                .map(Boolean::valueOf)
                .orElse(false);

        // remove some resource if necessary
        Iterator<Map.Entry<String, OperationResource>> iterator = operationResourceTemplate.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, OperationResource> operationResourceEntry = iterator.next();
            String resourceKey = operationResourceEntry.getKey();
            OperationResource operationResource = operationResourceEntry.getValue();
            // remove, if user is novice and this resource enabled novice protected
            if (operationResource.isProtected(isNovice)) {
                iterator.remove();
                continue;
            }
            // remove, if user selected resource
            if (!CollectionUtils.isEmpty(resourceParameters) && !resourceParameters.contains(resourceKey)) {
                iterator.remove();
                continue;
            }
        }

        Map<String, OperationResource> operableResources = new LinkedHashMap<>();

        // only select one, if event equals npc fixed event
        if (event instanceof NPCFixedEvent) {
            operationResourceTemplate.entrySet().stream()
                    .findFirst()
                    .ifPresent(entry -> operableResources.put(entry.getKey(), entry.getValue()));
        } else {
            operableResources.putAll(operationResourceTemplate);
        }
        return operableResources;
    }

    /**
     * 返回可执行的资源
     * <p>
     * 基于可操作的资源基础上，再过滤掉不需要等待刷新的资源（即可立即执行的资源）
     * 过滤条件更动态，需要实时计算
     *
     * @param event
     * @param operableResources 可操作的资源 {@link DefaultApplicationListenerHelper#getOperableResources(AbstractEvent, Map)}
     * @return executable resources
     */
    private Collection<OperationResource> getExecutableResources(AbstractEvent event,
                                                                 Collection<OperationResource> operableResources) {
        return operableResources.stream()
                // exclude waiting for refresh
                .filter(operationResource -> !operationResource.isWaitingForResourceRefresh())
                // sort by priority desc
                .sorted(Comparator.comparing(OperationResource::getPriority).reversed())
                .collect(Collectors.toList());
    }

    private void printLogForNextExecuteResource(AbstractEvent event,
                                                Collection<OperationResource> executableResources) {
        for (OperationResource executableResource : executableResources) {
            String sid = event.eventContext().getUser().getSid();
            String eventName = event.eventContext().getEventName();
            String operateName = executableResource.getOperateName();
            long refreshFrequency = executableResource.getRefreshFrequency();
            LocalDateTime lastRunTime = executableResource.getLastRunTime();
            log.debug("next execute [sid={}] [event={}] [operation={}] [last run time={}] [refresh frequency={}] ",
                    sid, eventName, operateName, lastRunTime, refreshFrequency);
        }
    }

    private boolean isEnterSleep(AbstractEvent event,
                                 Map<String, OperationResource> operableResources) {
        String sid = event.eventContext().getUser().getSid();
        long targetRunRound = event.eventContext().getTargetRunRound();
        long actualRunRound = event.eventContext().getActualRunRound();
        if (targetRunRound == -1) {
            return event.isNeedSleep()
                    && eventBus.isRunningEvent(sid)
                    && realTimeComputingSleepSeconds(event, operableResources) > 0;
        }
        return event.isNeedSleep()
                && eventBus.isRunningEvent(sid)
                && targetRunRound > actualRunRound
                && realTimeComputingSleepSeconds(event, operableResources) > 0;
    }

    private Long realTimeComputingSleepSeconds(AbstractEvent event,
                                               Map<String, OperationResource> operableResources) {
        Collection<OperationResource> executableResources = getExecutableResources(event, operableResources.values());
        // don't sleep, if has executable resource
        if (!CollectionUtils.isEmpty(executableResources)) {
            return 0L;
        }
        // real time computing best right sleep time, if hasn't executable resource
        ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
        return operableResources.values()
                .stream()
                .map(OperationResource::nextRunTime)
                .min(LocalDateTime::compareTo)
                .map(minNextRunTime -> minNextRunTime.toEpochSecond(zoneOffset) - LocalDateTime.now().toEpochSecond(zoneOffset))
                .orElse(0L);
    }

}
