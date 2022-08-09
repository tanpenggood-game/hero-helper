package com.itplh.hero.schedule;

import com.itplh.hero.context.HeroRegionUserContext;
import com.itplh.hero.event.core.OnlyRefreshEvent;
import com.itplh.hero.listener.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AutoRefreshSchedule {

    @Autowired
    private EventBus eventBus;

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void refresh() {
        HeroRegionUserContext.getAll().forEach(user -> {
            if (!eventBus.containsEvent(user.getSid())) {
                eventBus.publishEvent(OnlyRefreshEvent.refreshOnce(user.getSid()));
            }
        });
    }

}
