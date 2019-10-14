package cn.edu.buaa.act.data.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * CustomFileRepresentation
 *
 * @author wsj
 * @date 2018/10/24
 */
@Getter
@Setter
public class CustomFileRepresentation {
    private String id;
    private String userId;
    private String name;
    private String originalName;
    private long length;
    private Date uploadDate;
    private String type;
}
