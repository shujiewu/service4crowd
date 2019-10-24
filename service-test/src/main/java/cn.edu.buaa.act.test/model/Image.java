package cn.edu.buaa.act.test.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {
    private String id;
    private String dataSetName;
    private String file_name;
    private int width;
    private int height;
    private byte[] blob;
}
