package jframe.qcloud.model;

/**
 * @author dzh
 * @date Aug 5, 2018 1:11:31 AM
 * @version 0.0.1
 */
public class QCloudResponse<T> {

    private String codeDesc;
    private String message;
    private T data;
    private Integer code;

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
