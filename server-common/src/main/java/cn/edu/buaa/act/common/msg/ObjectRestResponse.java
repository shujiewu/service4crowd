package cn.edu.buaa.act.common.msg;


import java.time.LocalDateTime;

/**
 * @author wsj
 */
public class ObjectRestResponse<T> extends BaseResponse {

    T data;
    boolean rel;

    public boolean isRel() {
        return rel;
    }

    public void setRel(boolean rel) {
        this.rel = rel;
    }

    public ObjectRestResponse(){

    }

    public ObjectRestResponse status(int status){
        super.setStatus(status);
        return this;
    }

    public ObjectRestResponse rel(boolean rel) {
        this.setRel(rel);
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
                ", rel=" + rel +
                '}';
    }
}
