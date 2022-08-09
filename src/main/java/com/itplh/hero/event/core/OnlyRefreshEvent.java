package com.itplh.hero.event.core;

import com.itplh.hero.context.HeroRegionUserContext;
import com.itplh.hero.domain.SimpleUser;
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
        if (HeroRegionUserContext.contains(sid)) {
            SimpleUser user = HeroRegionUserContext.get(sid).get().simpleUser();
            HeroEventContext eventContext = new HeroEventContext(user, OnlyRefreshEvent.class.getSimpleName(), 1, null);
            return new OnlyRefreshEvent(eventContext);
        }
        return null;
    }

}
