package com.itplh.hero.event;

import org.springframework.context.ApplicationEvent;

public abstract class AbstractEvent extends ApplicationEvent {

    public AbstractEvent(HeroEventContext source) {
        super(source);
    }

    public HeroEventContext eventContext() {
        return ((HeroEventContext) super.getSource());
    }

}
