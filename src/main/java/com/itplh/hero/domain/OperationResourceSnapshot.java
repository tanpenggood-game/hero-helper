package com.itplh.hero.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationResourceSnapshot {

    /**
     * 操作名称
     * <p>
     * mapping {@link OperationResource#getOperateName()}
     */
    private String operateName;
    /**
     * 上一次运行时间
     * <p>
     * mapping {@link OperationResource#getLastRunTime()}
     */
    private LocalDateTime lastRunTime;

    public OperationResourceSnapshot(String operateName, LocalDateTime lastRunTime) {
        this.operateName = operateName;
        this.lastRunTime = lastRunTime;
    }

}
