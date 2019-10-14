package cn.edu.buaa.act.management.exception;

import cn.edu.buaa.act.management.entity.ServiceRegistration;

public class ServiceAlreadyRegisteredException extends IllegalStateException {

	private static final long serialVersionUID = 1L;


	private String name;
	private String type;
	private String version;

	public ServiceAlreadyRegisteredException(ServiceRegistration previous) {
		this.name = previous.getName();
		this.type = previous.getType();
		this.version = previous.getVersion();
	}

	public ServiceAlreadyRegisteredException(String name,String type,String version) {
		this.name = name;
		this.type = type;
		this.version = version;
	}

	@Override
	public String getMessage() {
		return String.format("The '%s:%s:%s' application is already registered",type, name,version);
	}
}
