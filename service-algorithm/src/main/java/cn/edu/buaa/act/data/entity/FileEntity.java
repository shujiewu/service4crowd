package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FileEntity {
    private String id;
    private String userName;
    private String userId;
    private String fileName;
    private byte[] data;
    private int size;
    private Date createTime;

    // private int count;
    // private String metaName;
}
