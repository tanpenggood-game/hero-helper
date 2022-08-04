package com.itplh.hero.listener.core;

import com.itplh.hero.event.core.NPCDungeonEvent;
import com.itplh.hero.listener.ApplicationListenerHelper;
import com.itplh.hero.service.EventHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NPCDungeonEventListener implements ApplicationListener<NPCDungeonEvent> {

    @Autowired
    private ApplicationListenerHelper applicationListenerHelper;

    @Autowired
    private EventHandleService eventHandleService;

    @Override
    public void onApplicationEvent(NPCDungeonEvent event) {
        applicationListenerHelper.doOnApplicationEvent(event,
                executableResources -> eventHandleService.handle(event, executableResources));
    }

}
