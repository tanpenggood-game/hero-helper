package com.itplh.hero.event.core;

import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;

/**
 * 刷固定NPC
 */
public class NPCFixedEvent extends AbstractEvent {

    public NPCFixedEvent(HeroEventContext source) {
        super(source);
    }

}
