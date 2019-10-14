package cn.edu.buaa.act.figureeight.exception;

/**
 * @author wsj
 */
public class CrowdFlowerException extends Exception {

    private static final long serialVersionUID = 1L;

    private String exception;

    public CrowdFlowerException(String exception)
    {
        super();
        this.exception = exception;
    }

    @Override
    public String toString()
    {
        return "CrowdFlowerException: " + exception;
    }

}
