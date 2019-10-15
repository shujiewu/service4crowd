package cn.edu.buaa.act.common.msg;


import java.time.LocalDateTime;

/**
 * @author wsj
 */
public class ObjectRestResponse<T> extends BaseResponse {

    T data;
    boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean rel) {
        this.success = rel;
    }

    public ObjectRestResponse(){

    }

    public ObjectRestResponse status(int status){
        super.setStatus(status);
        return this;
    }

    public ObjectRestResponse success(boolean rel) {
        this.setSuccess(rel);
        return this;
    }


    public ObjectRestResponse data(T data) {
        this.setTimestamp(LocalDateTime.now().toString());
        this.setData(data);
        return this;
    }
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObjectRestResponse{" +
                "data=" + data +
                ", rel=" + success +
                '}';
    }
}
