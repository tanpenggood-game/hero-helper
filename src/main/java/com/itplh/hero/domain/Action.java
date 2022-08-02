package com.itplh.hero.domain;

import com.itplh.hero.constant.DirectionEnum;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class Action {

    private DirectionEnum direction;
    /**
     * 是否执行回调操作
     * <p>
     * 默认值为true，表示执行回调函数
     */
    private boolean hasCallback = true;
    /**
     * 动作次数
     * <p>
     * 默认值为1，表示不重复该动作
     * repeatTimes = 2, 表示连续2次一样的动作
     */
    private int repeatTimes = 1;
    /**
     * 操作对象
     */
    private List<String> operationObjects = Collections.EMPTY_LIST;
    /**
     * 操作步骤
     */
    private List<String> operationSteps = Collections.EMPTY_LIST;

}


