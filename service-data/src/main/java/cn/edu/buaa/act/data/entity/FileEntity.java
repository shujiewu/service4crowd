package cn.edu.buaa.act.data.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wsj
 * 文件
 */
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
}
