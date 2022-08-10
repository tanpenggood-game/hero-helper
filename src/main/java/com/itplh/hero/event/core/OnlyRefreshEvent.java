package com.itplh.hero.event.core;

import com.itplh.hero.component.BeanUtil;
import com.itplh.hero.domain.SimpleUser;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;
import com.itplh.hero.service.HeroRegionUserService;

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
        HeroRegionUserService heroRegionUserService = BeanUtil.getBean(HeroRegionUserService.class);
        if (heroRegionUserService.contains(sid)) {
            SimpleUser user = heroRegionUserService.get(sid).get().simpleUser();
            HeroEventContext eventContext = new HeroEventContext(user, OnlyRefreshEvent.class.getSimpleName(), 1, null);
            return new OnlyRefreshEvent(eventContext);
        }
        return null;
    }

}
