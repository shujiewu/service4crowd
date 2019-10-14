package cn.edu.buaa.act.management.exception;


public class NoServiceDefinitionException extends RuntimeException {

	public NoServiceDefinitionException(String name) {
		super(String.format("Could not find definition named %s", name));
	}
}
