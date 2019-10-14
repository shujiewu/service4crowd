package cn.edu.buaa.act.workflow.exception;



public class NotFoundException extends BaseModelerRestException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
	}

	public NotFoundException(String s) {
		super(s);
	}
}