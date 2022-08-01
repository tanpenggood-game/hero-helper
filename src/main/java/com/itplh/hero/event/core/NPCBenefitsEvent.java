package com.itplh.hero.event.core;

import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;

/**
 * 每日福利
 */
public class NPCBenefitsEvent extends AbstractEvent {

    public NPCBenefitsEvent(HeroEventContext source) {
        super(source);
    }

}
