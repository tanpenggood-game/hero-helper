package com.itplh.hero.listener.core;

import com.itplh.hero.event.core.OnlyRefreshEvent;
import com.itplh.hero.listener.ApplicationListenerHelper;
import com.itplh.hero.service.EventHandleService;
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

    @Override
    public void onApplicationEvent(OnlyRefreshEvent event) {
        applicationListenerHelper.doOnApplicationEvent(event,
                executableResources -> eventHandleService.handle(event, executableResources));
    }

}
