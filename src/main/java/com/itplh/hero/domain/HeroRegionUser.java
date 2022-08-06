package com.itplh.hero.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class HeroRegionUser {

    /**
     * 用户身份标识
     */
    private String sid;
    /**
     * 登陆账号
     */
    private String username;
    /**
     * 账号密码
     */
    @JsonIgnore
    private String password;
    /**
     * 网络协议
     * 默认值为http
     */
    private String scheme = "http";
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

    private long timestamp = System.currentTimeMillis();

}
