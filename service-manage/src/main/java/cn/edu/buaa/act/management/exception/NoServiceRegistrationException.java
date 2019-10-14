package cn.edu.buaa.act.management.exception;

import cn.edu.buaa.act.management.common.ServiceType;


/**
 * @author wsj
 */
public class NoServiceRegistrationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoServiceRegistrationException(String name, String  type) {
		super(String.format("The '%s:%s' application could not be found.", type, name));
	}

	public NoServiceRegistrationException(String name, String type, String version) {
		super(String.format("The '%s:%s:%s' application could not be found.", type, name, version));
	}
}
