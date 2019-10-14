CREATE DATABASE service4crowd DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
Use service4crowd;

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  mobile_phone varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  type char(1) DEFAULT NULL,
  status char(1) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  crt_time datetime DEFAULT NULL,
  crt_user varchar(255) DEFAULT NULL,
  crt_host varchar(255) DEFAULT NULL,
  upd_time datetime DEFAULT NULL,
  upd_user varchar(255) DEFAULT NULL,
  upd_host varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- BEGIN;
-- INSERT INTO `base_user` VALUES ('1', 'admin', '$2a$12$S/yLlj9kzi5Dgsz97H4rAekxrPlk/10eXp1lUJcAVAx.2M9tOpWie', 'Mr.AG', '', null, '', null, '', '男', null, null, '', null, null, null, null, '2017-11-16 23:23:49', '1', 'Mr.AG', '127.0.0.1', null, null, null, null, null, null, null, null), ('2', 'test', '$2a$12$zWe6knO6rGp15UVfdWTTxu.Ykt.k3QnD5FPoj6a1cnL63csHY2A1S', '测试账户', '', null, '', null, '', '男', null, null, '', null, null, null, null, '2017-07-15 19:18:07', '1', '管理员', '0:0:0:0:0:0:0:1', null, null, null, null, null, null, null, null), ('4', 'blog', '$2a$12$S/yLlj9kzi5Dgsz97H4rAekxrPlk/10eXp1lUJcAVAx.2M9tOpWie', 'Mr.AG(博主)', '', null, '', null, '', '女', null, null, '12', null, null, null, null, '2018-03-07 11:21:10', '1', 'Mr.AG', '127.0.0.1', null, null, null, null, null, null, null, null);
-- COMMIT;
DROP TABLE IF EXISTS client;
CREATE TABLE client (
  id int(11) NOT NULL AUTO_INCREMENT,
  code varchar(255) DEFAULT NULL COMMENT '服务编码',
  secret varchar(255) DEFAULT NULL COMMENT '服务密钥',
  name varchar(255) DEFAULT NULL COMMENT '服务名',
  locked char(1) DEFAULT NULL COMMENT '是否锁定',
  description varchar(255) DEFAULT NULL COMMENT '描述',
  crt_time datetime DEFAULT NULL COMMENT '创建时间',
  crt_user varchar(255) DEFAULT NULL COMMENT '创建人',
  crt_host varchar(255) DEFAULT NULL COMMENT '创建主机',
  upd_time datetime DEFAULT NULL COMMENT '更新时间',
  upd_user varchar(255) DEFAULT NULL COMMENT '更新人',
  upd_host varchar(255) DEFAULT NULL COMMENT '更新主机',
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

