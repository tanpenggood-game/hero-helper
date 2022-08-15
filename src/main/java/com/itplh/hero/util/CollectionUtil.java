package com.itplh.hero.util;

import com.itplh.hero.constant.ParameterEnum;
import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;

public class CollectionUtil {

    public static String getLog(Collection<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        StringBuilder builder = new StringBuilder("robot request-");
        for (String text : list) {
            builder.append(text).append("、");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static LinkedHashMap<String, OperationResource> deepCopy(Map<String, OperationResource> map) {
        if (CollectionUtils.isEmpty(map)) {
            return null;
        }
        LinkedHashMap<String, OperationResource> newMap = new LinkedHashMap<>();
        map.forEach((k, originalValue) -> {
            OperationResource newValue = new OperationResource();
            BeanUtils.copyProperties(originalValue, newValue);
            newMap.put(k, newValue);
        });
        return newMap;
    }

    public static <E> Collection<E> merge(Collection<E>... collection) {
        List<E> result = new ArrayList<>();
        for (Collection c : collection) {
            result.addAll(c);
        }
        return result;
    }

    public static Map<String, OperationResource> removeResourceIfNecessary(AbstractEvent event,
                                                                           Map<String, OperationResource> operationResourceTemplate) {
        // parse user input parameters
        List userSelectedResources = event.eventContext()
                .queryExtendInfo(ParameterEnum.RESOURCES)
                .map(e -> e.split(","))
                .map(Arrays::asList)
                .orElse(Collections.EMPTY_LIST);
        boolean isExclude = event.eventContext().queryExtendInfo(ParameterEnum.EXCLUDE).map(Boolean::valueOf)
                .orElse(false);
        boolean isNovice = event.eventContext().queryExtendInfo(ParameterEnum.NOVICE).map(Boolean::valueOf)
                .orElse(false);

        // remove, if selected novice and current resource enabled novice protected
        removeIfNecessary(operationResourceTemplate, operationResourceEntry -> operationResourceEntry.getValue().isProtected(isNovice));
        // remove, if user selected resources
        if (!CollectionUtils.isEmpty(userSelectedResources)) {
            removeIfNecessary(operationResourceTemplate, operationResourceEntry -> {
                String resourceKey = operationResourceEntry.getKey();
                if (isExclude) {
                    // excludes user select's resources
                    return userSelectedResources.contains(resourceKey);
                }
                // only keep user select's resources
                return !userSelectedResources.contains(resourceKey);
            });
        }

        return operationResourceTemplate;
    }

    public static <K, V> Map<K, V> removeIfNecessary(Map<K, V> map,
                                                     Predicate<Map.Entry<K, V>> predicate) {
        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            // remove, if met
            if (predicate.test(iterator.next())) {
                iterator.remove();
            }
        }
        return map;
    }

}
