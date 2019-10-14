package cn.edu.buaa.act.hadoop.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wsj
 */
@Getter
@Setter
public class HDFSFileStatus {

	// 文件或目录路径
    private String path;
	// hdfs副本数
	private Short replication;
	// 是否是目录
	private boolean isDirectory;
	// 文件或目录长度
	private long len;
	// 文件大小
	private String size;
	// 当前所属用户
	private String owner;
	// 当前所属用户组
	private String group;
	// 权限
	private String permission;
	// 创建时间
	private long accessTime;
	// 最后修改时间
	private long modificationTime;
	// NameNode块大小
	private long blockSize;
	// 读权限
	private boolean readAccess;
	// 写权限
	private boolean writeAccess;
	// 执行权限
	private boolean executeAcess;

	public HDFSFileStatus(){
		
	}
}