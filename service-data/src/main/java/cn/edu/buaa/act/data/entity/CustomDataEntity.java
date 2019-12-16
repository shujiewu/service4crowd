package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * CustomDataEntity
 *
 * @author wsj
 * @date 2018/10/24
 */
@Getter
@Setter
public class CustomDataEntity {
    private String name;
    private Date createTime;
    private String id;
    private String userId;
    private List<CustomFile> customFiles;
    private List<String> fileId;
}
