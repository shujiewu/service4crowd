package cn.edu.buaa.act.data.exception;


import cn.edu.buaa.act.common.exception.BaseException;
import cn.edu.buaa.act.data.common.Constants;

/**
 * @author wsj
 */
public class NoAlgorithmException extends BaseException {

	private static final long serialVersionUID = 1L;
	public NoAlgorithmException(String message) {
		super(message, Constants.EX_ALG_NOT_FOUND_CODE);
	}
	public NoAlgorithmException(String name, String version) {
		super(String.format("The '%s:%s:%s' algorithm could not be found.", name, version),Constants.EX_ALG_NOT_FOUND_CODE);
	}
}
