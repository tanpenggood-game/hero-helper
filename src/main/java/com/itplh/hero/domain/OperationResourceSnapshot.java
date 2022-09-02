package com.itplh.hero.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jsoup.nodes.Document;

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
    /**
     * 当前网页
     */
    @JsonIgnore
    private Document currentDocument;

    public OperationResourceSnapshot(String operateName, LocalDateTime lastRunTime) {
        this.operateName = operateName;
        this.lastRunTime = lastRunTime;
    }

}
