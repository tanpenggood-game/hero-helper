package com.itplh.hero.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BeanUtil implements ApplicationContextAware {


    public static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        try {
            return (T) applicationContext.getBean(clazz);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtil.applicationContext = applicationContext;
    }

}
