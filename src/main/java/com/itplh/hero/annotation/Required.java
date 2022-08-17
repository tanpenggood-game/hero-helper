package com.itplh.hero.annotation;

import java.lang.annotation.*;

/**
 * 表示该参数为必须的
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Required {
}
