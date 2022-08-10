package com.itplh.hero.schedule;

import com.itplh.hero.event.core.OnlyRefreshEvent;
import com.itplh.hero.listener.EventBus;
import com.itplh.hero.service.HeroRegionUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class AutoRefreshSchedule {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private HeroRegionUserService heroRegionUserService;

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void refresh() {
        heroRegionUserService.getAll().forEach(user -> {
            if (!eventBus.containsEvent(user.getSid())) {
                eventBus.publishEvent(OnlyRefreshEvent.refreshOnce(user.getSid()));
            }
        });
    }

}
