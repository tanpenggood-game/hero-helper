package com.itplh.hero.domain;

import com.itplh.hero.annotation.Required;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class EventTriggerBody {

    @Required
    private String eventName;
    @Required
    private Collection<String> sids;
    /**
     * 目标运行次数
     * <p>
     * 默认值为1，表示运行1次
     * -1表示无限次
     */
    private Long targetRunRound = 1L;
    /**
     * 额外的扩展信息
     * <p>
     * extendInfo parameter detail, please see {@link com.itplh.hero.constant.ParameterEnum}
     */
    private Map<String, String> extendInfo;

}
