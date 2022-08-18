CREATE TABLE IF NOT EXISTS `t_region_user`(
   `id` INT UNSIGNED AUTO_INCREMENT,
   `sid` VARCHAR(255) NOT NULL,
   `scheme` VARCHAR(16) NOT NULL COMMENT '网络协议',
   `domain` VARCHAR(16) NOT NULL COMMENT '域名',
   `port` INT NOT NULL COMMENT '端口',
   `username` VARCHAR(16) COMMENT '账号',
   `password` VARCHAR(16) COMMENT '密码',
   `role_name` VARCHAR(16) COMMENT '角色名称',
   `region` VARCHAR(64) COMMENT '游戏分区',
   `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
   `last_update_time` TIMESTAMP,
   PRIMARY KEY ( `id` ),
   KEY ( `sid` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';