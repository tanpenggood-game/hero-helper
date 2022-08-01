package com.itplh.hero.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ClassUtil {

    public static <T> Map<String, Class<? extends T>> findSubclasses(Class<T> baseClass, String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(baseClass));
        // scan in org.example.package
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        Map<String, Class<? extends T>> subclasses = new HashMap<>();
        for (BeanDefinition component : components) {
            try {
                Class cls = Class.forName(component.getBeanClassName());
                subclasses.put(cls.getSimpleName(), cls);
            } catch (ClassNotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }
        return subclasses;
    }

    public static <T> T newInstance(Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.newInstance();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return instance;
    }

}
