package com.itplh.hero.util;

import com.itplh.hero.domain.OperationResource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

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

}
