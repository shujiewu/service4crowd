
package cn.edu.buaa.act.workflow.exception;



public class BadRequestException extends BaseModelerRestException {

	private static final long serialVersionUID = 1L;

	public BadRequestException(String s) {
		super(s);
	}
	
	public BadRequestException(String message, String messageKey) {
	    this(message);
	    setMessageKey(messageKey);
	}
	
	public BadRequestException(String s, Throwable t) {
        super(s, t);
    }
}