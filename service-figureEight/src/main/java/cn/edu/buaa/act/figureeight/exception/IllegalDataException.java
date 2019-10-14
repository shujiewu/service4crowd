package cn.edu.buaa.act.figureeight.exception;

import org.json.JSONException;

public class IllegalDataException extends JSONException {

    private static final long serialVersionUID = 1L;

    public IllegalDataException(String data)
    {
        super(data);
    }

    @Override
    public String toString()
    {
        return "IllegalDataException: The incoming data is malformed or not accessible completely";
    }

}
