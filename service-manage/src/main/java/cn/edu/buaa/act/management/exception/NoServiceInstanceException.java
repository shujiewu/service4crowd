package cn.edu.buaa.act.management.exception;

/**
 * @author wsj
 */
public class NoServiceInstanceException extends RuntimeException {

	private final String name;

	public NoServiceInstanceException(String name) {
		this(name, "Could not find deployment for the named " + name);
	}

	public NoServiceInstanceException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
