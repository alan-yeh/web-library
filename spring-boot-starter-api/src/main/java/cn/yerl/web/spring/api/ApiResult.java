package cn.yerl.web.spring.api;

import cn.yerl.web.kit.StrKit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by alan on 2017/3/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult <T> implements Serializable {

    private static final long serialVersionUID = 905684413600884072L;

    private int status = ApiStatus.OK.getValue();
    private String desc = "执行成功";
    private T data;
    private Pager pager;
    private long timestamp;
    public long getTimestamp(){
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public T getData() {
        return data;
    }

    public Pager getPager() {
        return pager;
    }

    public static class Pager{
        private long pageIndex;
        private long pageSize;
        private long pageCount;
        private long itemCount;

        public long getPageIndex() {
            return pageIndex;
        }

        public long getPageSize() {
            return pageSize;
        }

        public long getPageCount() {
            return pageCount;
        }

        public long getItemCount() {
            return itemCount;
        }

        public Pager(long pageIndex, long pageSize, long pageCount, long itemCount) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
            this.pageCount = pageCount;
            this.itemCount = itemCount;
        }
    }
    public ApiResult(){
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResult(ApiStatus status, String desc) {
        this();
        this.status = status.getValue();
        this.desc = desc;
    }

    public ApiResult(ApiStatus status, String desc, T data) {
        this();
        this.status = status.getValue();
        this.desc = desc;
        this.data = data;
    }


    public ApiResult(T data) {
        this();
        this.data = data;
    }

    public ApiResult(T data, Pager pager){
        this();
        this.data = data;
        this.pager = pager;
    }

    public static <T> ApiResult<T> success(){
        return new ApiResult();
    }

    public static <T> ApiResult<T> success(String desc, T data){
        return new ApiResult<>(ApiStatus.OK, desc, data);
    }

    public static <T> ApiResult<T> success(T data){
        return new ApiResult(data);
    }

    public static <T > ApiResult<T> success(T data, Pager pager){
        return new ApiResult(data, pager);
    }

    public static ApiResult failure(ApiStatus status, String desc, Object... args) {
        return new ApiResult(status, StrKit.format(desc, args));
    }

    public static ApiResult failure(ApiStatus status, String desc) {
        return new ApiResult(status, desc);
    }

    public static ApiResult failure(String desc){
        return new ApiResult(ApiStatus.SERVER_ERROR, desc);
    }

    public static ApiResult failure(BindingResult result){
        return ApiResult.failure(ApiStatus.BAD_REQUEST, result.getFieldError().getDefaultMessage());
    }

    @JsonIgnore
    public boolean isSuccess(){
        return this.status == 200;
    }
}
