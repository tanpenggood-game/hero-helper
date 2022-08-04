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

}
