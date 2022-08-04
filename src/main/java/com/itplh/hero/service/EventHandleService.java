package com.itplh.hero.service;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;

import java.util.Collection;

public interface EventHandleService {

    /**
     * @param event
     * @param executableResources must executable resources, in other words,
     *                            these resources don't waiting for resource refresh {@link OperationResource#isWaitingForResourceRefresh()}
     *                            and them are unprotected {@link OperationResource#isProtected(boolean)}
     * @return
     */
    boolean handle(AbstractEvent event, Collection<OperationResource> executableResources);

}
