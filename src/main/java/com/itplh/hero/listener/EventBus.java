package com.itplh.hero.listener;

import com.itplh.hero.event.AbstractEvent;

import java.util.Collection;

public interface EventBus {

    boolean publishEvent(AbstractEvent event);

    boolean close(String sid);

    boolean pause(String sid);

    /**
     * restart pause event
     *
     * @param sid
     * @return
     */
    boolean restart(String sid);

    boolean isClosedEvent(String sid);

    boolean containsEvent(String sid);

    boolean isRunningEvent(String sid);

    boolean isPauseEvent(String sid);

    Collection<AbstractEvent> getAllEvent();

    AbstractEvent getEvent(String sid);

}
