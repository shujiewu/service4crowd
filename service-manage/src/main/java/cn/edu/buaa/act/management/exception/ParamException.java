package cn.edu.buaa.act.management.exception;

import cn.edu.buaa.act.management.entity.ServiceDefinition;

/**
 * @author wsj
 */
public class ParamException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	private String param;

	public ParamException() {
	}

	public ParamException(String param) {
		this.param = param;
	}

	@Override
	public String getMessage() {
		if(param==null){
			return "parm can not be null";
		}
		return String.format("The '%s' param can not be null",param);
	}

}
