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
     * 是否补给粮草
     * <p>
     * 默认值为false，表示战斗前不进行粮草补给
     * TODO 实现可配置化的粮草补给
     */
    private boolean isSupplyGrain = false;
    /**
     * 操作对象
     */
    private List<String> operationObjects = Collections.EMPTY_LIST;
    /**
     * 操作步骤
     */
    private List<String> operationSteps = Collections.EMPTY_LIST;

}


