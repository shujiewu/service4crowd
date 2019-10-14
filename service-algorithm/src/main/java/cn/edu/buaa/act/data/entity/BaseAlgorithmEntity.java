package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * BaseAlgorithmEntity
 *
 * @author wsj
 * @date 2018/9/20
 */
@Getter
@Setter
public class BaseAlgorithmEntity {
    private String id;
    /**
     * 创建用户
     */
    private String userName;
    private String userId;

    /**
     * 方法名
     */
    private String method;
    /**
     * 版本
     */
    private String tag;


    private int permission;
    /**
     * 如果权限是组
     */
    private String[] groupId;
    private Date createTime;

    private Integer version;
}
