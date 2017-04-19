package cn.yerl.web.spring.api;

import cn.yerl.web.kit.StrKit;

/**
 * Created by alan on 2017/3/13.
 */
public class ApiException extends RuntimeException {
    public ApiException(){
        this("");
    }

    public ApiException(String errorMsg, Object... args){
        this(ApiStatus.SERVER_ERROR, errorMsg, args);
    }

    public ApiException(ApiStatus status, String errorMsg, Object... args){
        this(status, StrKit.format(errorMsg, args), (Throwable)null);
    }

    public ApiException(String errorMsg, Throwable internalError){
        this(ApiStatus.SERVER_ERROR, errorMsg, internalError);
    }

    public ApiException(ApiStatus status, String errorMsg, Throwable internalError){
        super(errorMsg, internalError);
        this.status = status;
        this.errorMsg = errorMsg;
    }

    private ApiStatus status;
    private String errorMsg;


    public ApiStatus getStatus() {
        return status;
    }

    public void setStatus(ApiStatus status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 重写toString(), 输出错误信息, 方便Log
     * @return
     */
    @Override
    public String toString() {
        return new StringBuilder(super.toString()).append("\n")
                .append("   Status: ").append(status).append("\n")
                .append(" ErrorMsg: ").append(errorMsg)
                .toString();
    }
}
