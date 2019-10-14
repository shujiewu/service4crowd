package cn.edu.buaa.act.management.exception;

import cn.edu.buaa.act.management.entity.ServiceDefinition;
import cn.edu.buaa.act.management.entity.ServiceRegistration;

public class ServiceAlreadyDefinedException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	private final ServiceDefinition previous;

	public ServiceAlreadyDefinedException(ServiceDefinition previous) {
		this.previous = previous;
	}

	@Override
	public String getMessage() {
		return String.format("The '%s' application is already defined", previous.getName());
	}

	public ServiceDefinition getPrevious() {
		return previous;
	}
}
