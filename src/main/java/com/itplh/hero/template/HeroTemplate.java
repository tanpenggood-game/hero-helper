package com.itplh.hero.template;

import com.itplh.hero.domain.OperationResource;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.util.CollectionUtil;

import java.util.Map;

public interface HeroTemplate {


    /**
     * 获取操作资源
     * <p>
     * 建议实现方法从单例实例中深拷贝数据
     * 保证不同用户触发的事件数据隔离，并且不影响单例对象中的模版数据
     * <p>
     * 拷贝方法{@link CollectionUtil#deepCopy(Map)}
     *
     * @return
     */
    Map<String, OperationResource> getOperationResourceTemplate();

    /**
     * 对应的事件
     *
     * @return
     */
    Class<? extends AbstractEvent> bindEvent();

}
