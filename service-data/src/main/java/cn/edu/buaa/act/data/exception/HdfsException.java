package cn.edu.buaa.act.data.exception;

/**
 * @author wsj
 */
public class HdfsException extends Exception{
    public HdfsException(String message) {
	    super(message);
	  }
	public HdfsException(String message, Throwable cause) {
	    super(message, cause);
	  }
}