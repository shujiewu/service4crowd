package cn.edu.buaa.act.mlflow.exception;
import cn.edu.buaa.act.mlflow.domain.ExperimentEntity;

public class ExperimentAlreadyExistedException extends IllegalStateException {

	private static final long serialVersionUID = 1L;

	private final ExperimentEntity previous;

	public ExperimentAlreadyExistedException(ExperimentEntity previous) {
		this.previous = previous;
	}

	@Override
	public String getMessage() {
		return String.format("The '%s' experiment is already registered", previous.getName());
	}

	public ExperimentEntity getPrevious() {
		return previous;
	}
}
