package cn.edu.buaa.act.figureeight.exception;

/**
 * @author wsj
 */
public class NullAPIKeyException extends Exception {

    private static final long serialVersionUID = 1L;

    public NullAPIKeyException()
    {
        super();
    }

    @Override
    public String toString()
    {
        return "NullAPIKeyException: CrowdFlower's API KEY is not set";
    }

}
