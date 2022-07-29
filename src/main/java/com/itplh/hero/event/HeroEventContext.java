package com.itplh.hero.event;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;

@Data
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
     * 额外的扩展信息
     */
    private Map<String, String> extendInfo;

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

}
