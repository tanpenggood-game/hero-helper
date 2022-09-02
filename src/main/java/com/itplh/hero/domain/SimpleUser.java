package com.itplh.hero.domain;

import lombok.Data;

@Data
public class SimpleUser {

    /**
     * 用户身份标识
     */
    private String sid;
    /**
     * 协议
     */
    private String scheme;
    /**
     * 域名
     */
    private String domain;
    /**
     * 端口
     */
    private int port;
    /**
     * 游戏分区
     */
    private String region;
    /**
     * 角色名称
     */
    private String roleName;

}
