package cn.edu.buaa.act.management.exception;

public class NoServiceDeploymentException extends RuntimeException {

	private final String name;

	public NoServiceDeploymentException(String name) {
		this(name, "Could not find deployment for the named " + name);
	}

	public NoServiceDeploymentException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
