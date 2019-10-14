package cn.edu.buaa.act.workflow.exception;

public class NotPermittedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPermittedException(String message) {
        super(message);
    }

    public NotPermittedException(Throwable cause) {
        super(cause);
    }
    
    public NotPermittedException() {
        super();
    }
}
