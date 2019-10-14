package cn.edu.buaa.act.fastwash.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author wsj
 * 项目
 */
@Getter
@Setter
public class ProjectEntity {
    private String id;
    private String name;
    private String userId;
    private String userName;
    private String status;
    private Date createTime;
    private Date completeTime;

    private String dataSetName;
    private List<String> imageId;
}
