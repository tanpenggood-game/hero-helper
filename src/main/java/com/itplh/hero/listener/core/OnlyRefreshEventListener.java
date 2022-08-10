package com.itplh.hero.listener.core;

import com.itplh.hero.event.core.OnlyRefreshEvent;
import com.itplh.hero.listener.ApplicationListenerHelper;
import com.itplh.hero.listener.EventBus;
import com.itplh.hero.service.EventHandleService;
import com.itplh.hero.service.HeroRegionUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OnlyRefreshEventListener implements ApplicationListener<OnlyRefreshEvent> {

    @Autowired
    private EventHandleService eventHandleService;

    @Autowired
    private ApplicationListenerHelper applicationListenerHelper;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private HeroRegionUserService heroRegionUserService;

    @Override
    public void onApplicationEvent(OnlyRefreshEvent event) {
        applicationListenerHelper.doOnApplicationEvent(event,
                executableResources -> {
                    boolean isSuccess = eventHandleService.handle(event, executableResources);
                    if (!isSuccess) {
                        // delete user & close related event, if already offline
                        String sid = event.eventContext().getUser().getSid();
                        boolean isClosed = eventBus.close(sid);
                        boolean isDelete = heroRegionUserService.delete(sid);
                        log.info("find offline user [sid={}] [close event={}] [delete user={}]", sid, isClosed, isDelete);
                    }
                    return isSuccess;
                });
    }

}
