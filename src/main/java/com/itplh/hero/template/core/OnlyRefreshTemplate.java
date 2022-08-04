package com.itplh.hero.template.core;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.core.OnlyRefreshEvent;
import com.itplh.hero.template.HeroTemplate;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Data
@Component
public class OnlyRefreshTemplate implements HeroTemplate {

    @Override
    public Map<String, OperationResource> getOperationResourceTemplate() {
        OperationResource operationResource = new OperationResource();
        operationResource.setOperateName("only-refresh");
        // set refresh frequency 10 minutes
        operationResource.setRefreshFrequency(600L);
        Map<String, OperationResource> mapping = Collections.singletonMap("only-refresh", operationResource);

        return mapping;
    }

    @Override
    public Class<? extends AbstractEvent> bindEvent() {
        return OnlyRefreshEvent.class;
    }

}
