package cn.edu.buaa.act.mlflow.exception;


public class NoExperimentException extends RuntimeException {

	public NoExperimentException(String name) {
		super(String.format("Could not find definition named %s", name));
	}
}
