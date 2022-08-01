package com.itplh.hero.event.core;

import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;

/**
 * 刷副本
 */
public class NPCDungeonEvent extends AbstractEvent {

    public NPCDungeonEvent(HeroEventContext source) {
        super(source);
    }

}
