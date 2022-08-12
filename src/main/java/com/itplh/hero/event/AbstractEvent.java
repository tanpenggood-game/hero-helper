package com.itplh.hero.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.ApplicationEvent;

public abstract class AbstractEvent extends ApplicationEvent {

    public AbstractEvent(HeroEventContext source) {
        super(source);
    }

    public HeroEventContext eventContext() {
        return ((HeroEventContext) super.getSource());
    }

    /**
     * used to waiting for resource in each round.
     *
     * @return return true, this event meet sleep conditions; return false, needn't sleep.
     */
    @JsonIgnore
    public boolean isNeedSleep() {
        return false;
    }

}
