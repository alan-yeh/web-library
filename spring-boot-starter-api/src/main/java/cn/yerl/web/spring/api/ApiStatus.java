package cn.yerl.web.spring.api;

/**
 * Created by alan on 2017/3/13.
 */
public enum ApiStatus {
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    SERVER_ERROR(500);

    private int value;
    ApiStatus(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static ApiStatus valueOf(int value){
        switch (value){
            case 200:
                return OK;
            case 400:
                return BAD_REQUEST;
            case 401:
                return UNAUTHORIZED;
            case 403:
                return FORBIDDEN;
            case 404:
                return NOT_FOUND;
            case 500:
                return SERVER_ERROR;
        }
        return null;
    }
}