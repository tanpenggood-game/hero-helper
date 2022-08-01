package com.itplh.hero.util;

import com.itplh.hero.component.BeanUtil;
import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;
import com.itplh.hero.template.HeroTemplate;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class EventTemplateUtil {

    private static final Map<String, Class<? extends AbstractEvent>> eventMapping;
    private static final Map<String, Class<? extends HeroTemplate>> templateMapping;
    private static final Map<Class<? extends AbstractEvent>, Class<? extends HeroTemplate>> eventTemplateMapping;

    static {
        eventMapping = ClassUtil.findSubclasses(AbstractEvent.class, "com.itplh.hero.event.core");

        templateMapping = ClassUtil.findSubclasses(HeroTemplate.class, "com.itplh.hero.template.core");

        eventTemplateMapping = new HashMap<>();
        templateMapping.values().stream().forEach(templateClass -> {
            HeroTemplate heroTemplate = ClassUtil.newInstance(templateClass);
            Class<? extends AbstractEvent> eventClass = heroTemplate.bindEvent();
            eventTemplateMapping.put(eventClass, templateClass);
        });
    }

    public static boolean hasEventInstance(String eventName) {
        return eventMapping.containsKey(eventName);
    }

    public static Optional<AbstractEvent> getEventInstance(String eventName, HeroEventContext heroEventContext) {
        Optional<AbstractEvent> instanceOptional = Optional.empty();
        try {
            Class<? extends AbstractEvent> event = eventMapping.get(eventName);
            Constructor<? extends AbstractEvent> constructor = event.getDeclaredConstructor(HeroEventContext.class);
            AbstractEvent instance = constructor.newInstance(heroEventContext);
            instanceOptional = Optional.ofNullable(instance);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return instanceOptional;
    }

    public static Map<String, OperationResource> getOperationResourceTemplate(Class<? extends AbstractEvent> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        Class<? extends HeroTemplate> templateClass = eventTemplateMapping.get(clazz);
        HeroTemplate template = BeanUtil.getBean(templateClass);
        return template.getOperationResourceTemplate();
    }

}
