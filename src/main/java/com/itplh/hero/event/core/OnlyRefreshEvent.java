package com.itplh.hero.event.core;

import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;

/**
 * 刷新事件，用于挂机
 */
public class OnlyRefreshEvent extends AbstractEvent {

    public OnlyRefreshEvent(HeroEventContext source) {
        super(source);
    }

    @Override
    public boolean isNeedSleep() {
        return true;
    }

    public static OnlyRefreshEvent refreshOnce(String sid) {
        HeroEventContext eventContext = new HeroEventContext(sid, OnlyRefreshEvent.class.getSimpleName(), 1, null);
        return new OnlyRefreshEvent(eventContext);
    }

}
