package com.itplh.hero.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_region_user")
public class HeroRegionUser {

    @TableId(type = IdType.AUTO)
    private Integer id;

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

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdateTime;

    public SimpleUser simpleUser() {
        SimpleUser simpleUser = new SimpleUser();
        simpleUser.setSid(sid);
        simpleUser.setScheme(scheme);
        simpleUser.setDomain(domain);
        simpleUser.setPort(port);
        simpleUser.setRegion(region);
        simpleUser.setRoleName(roleName);
        return simpleUser;
    }

}
