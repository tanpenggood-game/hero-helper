package com.itplh.hero.service;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.Optional;

public interface EventHandleService {

    Optional<Document> handle(AbstractEvent event, OperationResource operationResource);

    /**
     * @param event
     * @param operationResourceMap
     * @return
     */
    boolean handle(AbstractEvent event, Map<String, OperationResource> operationResourceMap);

}
