package com.itplh.hero.listener;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;

import java.util.Collection;
import java.util.function.Function;

public interface ApplicationListenerHelper {

    void doOnApplicationEvent(AbstractEvent event,
                              Function<Collection<OperationResource>, Boolean> doBusiness);

}
