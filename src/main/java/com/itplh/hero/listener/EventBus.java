package com.itplh.hero.listener;

import com.itplh.hero.event.AbstractEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EventBus {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * save running's event
     * <p>
     * key sid, sid可以理解为用户的唯一标识
     */
    private static final ConcurrentHashMap<String, AbstractEvent> runningContainer = new ConcurrentHashMap<>();

    /**
     * save already pause's event
     * <p>
     * key sid
     */
    private static final ConcurrentHashMap<String, AbstractEvent> pauseContainer = new ConcurrentHashMap<>();

    public boolean publishEvent(AbstractEvent event) {
        return asyncPublishEvent(event.eventContext().getSid(), event);
    }

    public boolean close(String sid) {
        boolean isClose = Optional.ofNullable(EventBus.runningContainer.remove(sid)).isPresent();
        boolean isCloseWhenPause = Optional.ofNullable(EventBus.pauseContainer.remove(sid)).isPresent();
        return isClose || isCloseWhenPause;
    }

    public boolean pause(String sid) {
        if (containsInRunningContainer(sid)) {
            return Optional.ofNullable(runningContainer.remove(sid))
                    .map(runningEvent -> {
                        pauseContainer.put(sid, runningEvent);
                        return true;
                    }).orElse(false);
        }
        return false;
    }

    /**
     * restart pause event
     *
     * @param sid
     * @return
     */
    public boolean continue0(String sid) {
        if (containsInPauseContainer(sid)) {
            return Optional.ofNullable(pauseContainer.remove(sid))
                    .map(pauseEvent -> asyncPublishEvent(sid, pauseEvent))
                    .orElse(false);
        }
        return false;
    }

    public boolean contains(String sid) {
        return containsInRunningContainer(sid) || containsInPauseContainer(sid);
    }

    public boolean containsInRunningContainer(String sid) {
        return runningContainer.containsKey(sid);
    }

    public boolean containsInPauseContainer(String sid) {
        return pauseContainer.containsKey(sid);
    }

    public Map<String, Map<String, AbstractEvent>> getAllEvent() {
        Map<String, Map<String, AbstractEvent>> all = new HashMap<>();
        all.put("running", runningContainer);
        all.put("pause", pauseContainer);
        return Collections.unmodifiableMap(all);
    }

    public Map<String, AbstractEvent> getEvent(String sid) {
        if (runningContainer.containsKey(sid)) {
            return Collections.singletonMap("running", getRunningEvent(sid));
        }
        if (pauseContainer.containsKey(sid)) {
            return Collections.singletonMap("pause", getPauseEvent(sid));
        }
        return Collections.EMPTY_MAP;
    }

    public AbstractEvent getRunningEvent(String sid) {
        return runningContainer.get(sid);
    }

    public AbstractEvent getPauseEvent(String sid) {
        return pauseContainer.get(sid);
    }

    /**
     * @param sid
     * @param event
     * @return
     */
    private boolean asyncPublishEvent(String sid, AbstractEvent event) {
        if (contains(sid)) {
            // there is running or pause, warn for user
            String existedEvent = getEvent(sid).values().stream()
                    .findFirst()
                    .map(e -> e.eventContext().getEventName())
                    .orElse(null);
            log.warn("[sid={}] be doing [event={}]", sid, existedEvent);
            return false;
        }
        log.info("publish event [sid={}] [event={}]", sid, event.eventContext().getEventName());
        runningContainer.put(sid, event);
        // async running
        threadPoolTaskExecutor.execute(() -> applicationContext.publishEvent(event));
        return true;
    }

}
