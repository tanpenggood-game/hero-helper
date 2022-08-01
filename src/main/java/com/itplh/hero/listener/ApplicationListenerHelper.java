package com.itplh.hero.listener;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;
import com.itplh.hero.event.core.NPCTimedRefreshEvent;
import com.itplh.hero.util.EventTemplateUtil;
import com.itplh.hero.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Component
public class ApplicationListenerHelper {

    @Autowired
    private EventBus eventBus;

    public void doOnApplicationEvent(AbstractEvent event,
                                     Function<Map<String, OperationResource>, Boolean> doBusiness) {
        HeroEventContext heroEventContext = event.eventContext();
        if (!eventBus.contains(heroEventContext.getSid())) {
            return;
        }

        // 从Spring的单例实例中深拷贝数据，保证不同用户触发的事件数据隔离，并且不对单例对象中的模版数据产生影响
        Map<String, OperationResource> operationResourceTemplate = EventTemplateUtil.getOperationResourceTemplate(event.getClass());

        String sid = heroEventContext.getSid();
        String eventName = heroEventContext.getEventName();
        log.info("start [event={}] [sid={}]", eventName, sid);

        long targetRunRound = heroEventContext.getTargetRunRound();
        long successRunRound = heroEventContext.getSuccessRunRound();
        long actualRunRound = heroEventContext.getActualRunRound();
        while (eventBus.contains(sid)) {
            ++actualRunRound;
            // pause
            if (eventBus.containsInPauseContainer(sid)) {
                break;
            }
            // crate new event or restart event
            boolean isEnd = actualRunRound > targetRunRound && targetRunRound != -1;
            if (isEnd) {
                eventBus.close(sid);
                break;
            }
            // handle event
            try {
                boolean isSuccess = doBusiness.apply(operationResourceTemplate);
                // real time update success run count
                if (isSuccess) {
                    event.eventContext().setSuccessRunRound(++successRunRound);
                }
            } catch (Throwable e) {
                log.warn("No.{} round [event={}] exception, [sid={}] =======================", actualRunRound, eventName, sid);
                log.error(e.getMessage(), e);
            } finally {
                event.eventContext().setActualRunRound(actualRunRound);
                logAndSleepIfNecessary(operationResourceTemplate.values(), heroEventContext);
            }
        }

        log.info("finish [event={}] [sid={}]", eventName, sid);
    }

    private void logAndSleepIfNecessary(Collection<OperationResource> operationResources,
                                        HeroEventContext heroEventContext) {
        try {
            String sid = heroEventContext.getSid();
            String eventName = heroEventContext.getEventName();
            long actualRunRound = heroEventContext.getActualRunRound();
            boolean isEnterSleep = isEnterSleep(heroEventContext, operationResources);
            long sleepSeconds = isEnterSleep ? realTimeComputingSleepSeconds(operationResources) : 0L;
            log.info("No.{} round [event={}] finish, [sid={}] [sleep second={}] [enter sleep={}]",
                    actualRunRound, eventName, sid, sleepSeconds, isEnterSleep);

            while (isEnterSleep(heroEventContext, operationResources)) {
                ThreadUtil.sleep(3, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            // ignore
            log.error(e.getMessage(), e);
        }
    }

    private Long realTimeComputingSleepSeconds(Collection<OperationResource> operationResources) {
        Long minRefreshFrequency = operationResources.stream()
                .map(e -> e.getRefreshFrequency())
                .min(Long::compareTo)
                .orElse(0L);
        return operationResources.stream()
                .map(OperationResource::getLastRunTime)
                // exclude null
                .filter(time -> Optional.ofNullable(time).isPresent())
                // get min last run time
                .min(LocalDateTime::compareTo)
                .map(minLastRunTime -> minLastRunTime.toEpochSecond(ZoneOffset.ofHours(8)))
                .map(time -> time + minRefreshFrequency)
                .map(nextRunTimeStamp -> nextRunTimeStamp - (System.currentTimeMillis() / 1000))
                .orElse(0L);
    }

    private boolean isEnterSleep(HeroEventContext heroEventContext,
                                 Collection<OperationResource> operationResources) {
        String sid = heroEventContext.getSid();
        String eventName = heroEventContext.getEventName();
        long targetRunRound = heroEventContext.getTargetRunRound();
        long actualRunRound = heroEventContext.getActualRunRound();
        long sleepSeconds = realTimeComputingSleepSeconds(operationResources);
        if (targetRunRound == -1) {
            return Objects.equals(eventName, NPCTimedRefreshEvent.class.getSimpleName())
                    && eventBus.containsInRunningContainer(sid)
                    && sleepSeconds > 0;
        }
        return Objects.equals(eventName, NPCTimedRefreshEvent.class.getSimpleName())
                && eventBus.containsInRunningContainer(sid)
                && sleepSeconds > 0
                && targetRunRound > actualRunRound;
    }

}
