package com.itplh.hero.listener;

import com.itplh.hero.constant.EventStatusEnum;
import com.itplh.hero.event.AbstractEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DefaultEventBus implements EventBus {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * key sid, sid可以理解为用户的唯一标识
     */
    private static final ConcurrentHashMap<String, AbstractEvent> eventBusContainer = new ConcurrentHashMap<>();

    @Override
    public boolean publishEvent(AbstractEvent event) {
        return asyncPublishEvent(event.eventContext().getUser().getSid(), event);
    }

    @Override
    public boolean close(String sid) {
        return Optional.ofNullable(eventBusContainer.remove(sid)).isPresent();
    }

    @Override
    public boolean pause(String sid) {
        return getRunningEventOptional(sid)
                .map(event -> {
                    event.eventContext().setStatus(EventStatusEnum.PAUSE);
                    return true;
                }).orElse(false);
    }

    /**
     * restart pause event
     *
     * @param sid
     * @return
     */
    @Override
    public boolean restart(String sid) {
        return getPauseEventOptional(sid)
                .map(event -> asyncPublishEvent(sid, event))
                .orElse(false);
    }

    @Override
    public boolean isClosedEvent(String sid) {
        return Objects.isNull(getEvent(sid));
    }

    @Override
    public boolean containsEvent(String sid) {
        return getEventOptional(sid).isPresent();
    }

    @Override
    public boolean isRunningEvent(String sid) {
        return getRunningEventOptional(sid).isPresent();
    }

    @Override
    public boolean isPauseEvent(String sid) {
        return getPauseEventOptional(sid).isPresent();
    }

    @Override
    public Collection<AbstractEvent> getAllEvent() {
        return Collections.unmodifiableCollection(eventBusContainer.values());
    }

    @Override
    public AbstractEvent getEvent(String sid) {
        return getEventOptional(sid).orElse(null);
    }

    private Optional<AbstractEvent> getEventOptional(String sid) {
        return Optional.ofNullable(eventBusContainer.get(sid));
    }

    private Optional<AbstractEvent> getEventOptional(String sid, EventStatusEnum status) {
        return getEventOptional(sid)
                .filter(event -> Objects.equals(event.eventContext().getStatus(), status));
    }

    private Optional<AbstractEvent> getRunningEventOptional(String sid) {
        return getEventOptional(sid, EventStatusEnum.RUNNING);
    }

    private Optional<AbstractEvent> getPauseEventOptional(String sid) {
        return getEventOptional(sid, EventStatusEnum.PAUSE);
    }

    /**
     * @param sid
     * @param event
     * @return
     */
    private boolean asyncPublishEvent(String sid, AbstractEvent event) {
        // warning, if exists running event
        if (isRunningEvent(sid)) {
            AbstractEvent existedEvent = getEvent(sid);
            log.warn("exists running event [sid={}] [event={}]", sid, existedEvent.eventContext().getEventName());
            return false;
        }
        String eventLog = "published event";
        if (isPauseEvent(sid)) {
            String original = getEvent(sid).eventContext().getEventName();
            String current = event.eventContext().getEventName();
            if (Objects.equals(original, current)) {
                eventLog = "restart be paused event";
            } else {
                log.warn("restart event inconsistency [sid={}] [original={}] [current={}]", sid, original, current);
                return false;
            }
        }
        // create new event or restart be paused event
        event.eventContext().setStatus(EventStatusEnum.RUNNING);
        eventBusContainer.put(sid, event);
        // async running
        threadPoolTaskExecutor.execute(() -> applicationContext.publishEvent(event));
        log.info("{} [sid={}] [event={}]", eventLog, sid, event.eventContext().getEventName());
        return true;
    }

}
