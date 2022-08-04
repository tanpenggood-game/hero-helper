package com.itplh.hero.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itplh.hero.constant.EventStatusEnum;
import com.itplh.hero.constant.ParameterEnum;
import com.itplh.hero.domain.OperationResourceSnapshot;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class HeroEventContext {

    private String sid;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 目标运行次数，-1为无限次数
     */
    private long targetRunRound;
    /**
     * 已成功运行次数
     */
    private long successRunRound;
    /**
     * 实际运行次数
     */
    private long actualRunRound;
    /**
     * 事件状态
     * <p>
     * event status reference {@link EventStatusEnum}
     */
    private EventStatusEnum status;
    /**
     * 额外的扩展信息
     * <p>
     * extendInfo parameter includes parameter as follow:
     * 1. mapping, used to specify the operation object
     * 2. novice, set value as true, will enable novice protection when operation resource
     * 3. isSupplyGrain, set value as true, will be supplied grain before operate every resource
     * or see {@link ParameterEnum}
     */
    private Map<String, String> extendInfo;
    /**
     * 操作快照
     * <p>
     * main used to event pause and restart
     * record operate snapshot before pause, and replay operate after restart
     */
    @JsonIgnore
    private Map<String, OperationResourceSnapshot> operationSnapshotMap;

    public HeroEventContext(String sid, String eventName, long targetRunRound, Map<String, String> extendInfo) {
        this.sid = sid;
        this.eventName = eventName;
        this.targetRunRound = targetRunRound;
        this.extendInfo = CollectionUtils.isEmpty(extendInfo) ? Collections.EMPTY_MAP : extendInfo;
    }

    public static HeroEventContext newInstance(String sid, String eventName, long targetRunRound, Map<String, String> extendInfo) {
        return new HeroEventContext(sid, eventName, targetRunRound, extendInfo);
    }

    public String buildURI(String sid) {
        return "/gCmd.do?cmd=1&sid=" + sid;
    }

    public String buildURI() {
        return buildURI(sid);
    }

    public Optional<String> queryExtendInfo(ParameterEnum parameter) {
        String value = extendInfo.get(parameter.getName());
        return StringUtils.hasText(value) ? Optional.ofNullable(value) : Optional.empty();
    }

}
