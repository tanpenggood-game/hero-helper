package com.itplh.hero.util;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.domain.OperationResourceSnapshot;
import com.itplh.hero.event.AbstractEvent;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnapshotUtil {

    /**
     * save snapshot
     *
     * @param event
     * @param operationResourceTemplate
     */
    public static void saveSnapshot(AbstractEvent event,
                                    Map<String, OperationResource> operationResourceTemplate) {
        Map<String, OperationResourceSnapshot> resourceSnapshotMap = new HashMap<>();
        operationResourceTemplate.entrySet().stream()
                .forEach(entry -> {
                    String resourceKey = entry.getKey();
                    String operateName = entry.getValue().getOperateName();
                    LocalDateTime lastRunTime = entry.getValue().getLastRunTime();
                    resourceSnapshotMap.put(resourceKey, new OperationResourceSnapshot(operateName, lastRunTime));
                });
        event.eventContext().setOperationSnapshotMap(resourceSnapshotMap);
    }


    /**
     * replay operation resource snapshot, if not empty
     *
     * @param event
     * @param operationResourceTemplate
     */
    public static void replaySnapshot(AbstractEvent event,
                                      Map<String, OperationResource> operationResourceTemplate) {
        Map<String, OperationResourceSnapshot> operationSnapshotMap = event.eventContext().getOperationSnapshotMap();
        if (CollectionUtils.isEmpty(operationSnapshotMap)) {
            return;
        }
        operationSnapshotMap.entrySet().forEach(entry -> {
            String resourceKey = entry.getKey();
            LocalDateTime lastRunTime = entry.getValue().getLastRunTime();
            Optional.ofNullable(operationResourceTemplate.get(resourceKey))
                    .ifPresent(operationResource -> operationResource.setLastRunTime(lastRunTime));
        });
        // clear last snapshot
        event.eventContext().setOperationSnapshotMap(null);
    }

}
