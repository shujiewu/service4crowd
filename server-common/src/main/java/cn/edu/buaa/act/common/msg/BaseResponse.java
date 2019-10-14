package cn.edu.buaa.act.common.msg;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author wsj
 */
public class BaseResponse {
    private int status = 200;
    private String message;
    private String timestamp;
    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public BaseResponse() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
