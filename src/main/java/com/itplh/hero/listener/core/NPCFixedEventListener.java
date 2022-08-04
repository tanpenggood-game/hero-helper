package com.itplh.hero.listener.core;

import com.itplh.hero.event.core.NPCFixedEvent;
import com.itplh.hero.listener.ApplicationListenerHelper;
import com.itplh.hero.service.EventHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NPCFixedEventListener implements ApplicationListener<NPCFixedEvent> {

    @Autowired
    private ApplicationListenerHelper applicationListenerHelper;

    @Autowired
    private EventHandleService eventHandleService;

    @Override
    public void onApplicationEvent(NPCFixedEvent event) {
        applicationListenerHelper.doOnApplicationEvent(event,
                executableResources -> eventHandleService.handle(event, executableResources));
    }

}
