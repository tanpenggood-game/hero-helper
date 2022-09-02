package com.itplh.hero.domain;

import com.itplh.hero.constant.WorldMapEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

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
     * 全局操作步骤
     */
    private List<String> globalOperationSteps = Collections.EMPTY_LIST;
    /**
     * 行动路线
     */
    private List<Action> actions = Collections.EMPTY_LIST;
    /**
     * 上一次运行时间
     */
    private LocalDateTime lastRunTime;

    public boolean isFixedResource() {
        return refreshFrequency <= 0L;
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
        return Optional.ofNullable(nextRunTime())
                .map(nextRunTime -> {
                    ZoneOffset zoneOffset = ZoneOffset.ofHours(8);
                    long nextRunTimeStamp = nextRunTime.toEpochSecond(zoneOffset);
                    long nowTimeStamp = LocalDateTime.now().toEpochSecond(zoneOffset);
                    return (nextRunTimeStamp - nowTimeStamp) > 0;
                })
                .orElse(true);
    }

    /**
     * 下一次可运行时间
     *
     * @return
     */
    public LocalDateTime nextRunTime() {
        LocalDateTime nextRunTime = LocalDateTime.now();
        if (isFixedResource() || Objects.isNull(lastRunTime)) {
            return nextRunTime;
        }
        nextRunTime = lastRunTime.plusSeconds(refreshFrequency);
        return nextRunTime;
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

    public List<Action> allActions() {
        List<Action> allActions = new ArrayList<>();
        for (Action action : actions) {
            IntStream.range(0, action.getRepeatTimes())
                    .forEach(n -> allActions.add(action));
        }
        return allActions;
    }

}
