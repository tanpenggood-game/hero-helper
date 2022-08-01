package com.itplh.hero.event.core;

import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;

/**
 * 定时刷新的NPC
 */
public class NPCTimedRefreshEvent extends AbstractEvent {

    public NPCTimedRefreshEvent(HeroEventContext source) {
        super(source);
    }

}
