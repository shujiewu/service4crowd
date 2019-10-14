package cn.edu.buaa.act.figureeight.exception;

import java.net.MalformedURLException;

/**
 * @author wsj
 */
public class MalformedCrowdURLException extends MalformedURLException {

    private static final long serialVersionUID = 1L;
    private String URL;

    public MalformedCrowdURLException(String URL)
    {
        super(URL);
        this.URL = URL;
    }

    @Override
    public String toString()
    {
        return "MalformedCrowdURLException: Crowd web service URL is" + URL
               + "is malformed";

    }
}
