package com.itplh.hero.domain;

import com.itplh.hero.constant.WorldMapEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Slf4j
public class OperationResource {

    /**
     * 操作名称
     */
    private String operateName;
    /**
     * 起始位置
     */
    private WorldMapEnum startPosition;
    /**
     * 刷新频率(秒)
     * <p>
     * 默认值为0，表示固定的NPC
     */
    private long refreshFrequency;
    /**
     * 次数限制
     * <p>
     * 默认值为-1，表示不限制次数
     * recommend set limit, if resource is dungeon
     * TODO 实现次数限制
     */
    private int limit = -1;
    /**
     * 优先级
     * <p>
     * 值越大，越优先操作
     */
    private int priority = 0;
    /**
     * 新手保护
     * <p>
     * 默认值为false，表示不启用
     * recommend set value as true, if npc is large monster
     */
    private boolean enableNoviceProtection;
    /**
     * 全局操作对象
     */
    private List<String> globalOperationObjects = Collections.EMPTY_LIST;
    /**
     * 行动路线
     */
    private List<Action> actions = Collections.EMPTY_LIST;
    /**
     * 上一次运行时间
     */
    private LocalDateTime lastRunTime;

    public boolean isFixedResource() {
        return refreshFrequency == 0L && limit == -1;
    }

    /**
     * 是否等待资源刷新
     * <p>
     * 1. don't wait, if fixed resource
     * 2. don't wait, if never operated once
     *
     * @return true, need waiting resource refresh; false, don't wait
     */
    public boolean isWaitingForResourceRefresh() {
        if (isFixedResource() || Objects.isNull(lastRunTime)) {
            return false;
        }
        return Optional.ofNullable(lastRunTime)
                .map(lastRunTime -> lastRunTime.plusSeconds(refreshFrequency))
                .map(nextRunTime -> {
                    ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
                    long nextRunTimeStamp = nextRunTime.toEpochSecond(zoneOffset);
                    long nowTimeStamp = LocalDateTime.now().toEpochSecond(zoneOffset);
                    return (nextRunTimeStamp - nowTimeStamp) > 0;
                })
                .orElse(true);
    }

    /**
     * will be protected if enabled novice protection and role is novice.
     *
     * @param isNovice
     * @return
     */
    public boolean isProtected(boolean isNovice) {
        return enableNoviceProtection && isNovice;
    }

    public boolean isUnprotected(boolean isNovice) {
        return !isProtected(isNovice);
    }

}

