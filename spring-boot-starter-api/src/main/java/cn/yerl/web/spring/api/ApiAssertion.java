package cn.yerl.web.spring.api;

/**
 * Created by alan on 2017/3/13.
 */
public class ApiAssertion {
    public static void assertTrue(boolean condition, String errorMsg, Object... args){
        assertTrue(condition, ApiStatus.SERVER_ERROR, errorMsg, args);
    }

    public static void assertTrue(boolean condition, ApiStatus status, String errorMsg, Object... args){
        if (!condition)
            throw new ApiException(status, errorMsg, args);
    }

    public static void assertEquals(Object obj1, Object obj2, String errorMsg, Object... args){
        assertEquals(obj1, obj2, ApiStatus.SERVER_ERROR, errorMsg, args);
    }

    public static void assertEquals(Object obj1, Object obj2, ApiStatus status, String errorMsg, Object... args){
        if (obj1 == null && obj2 == null)
            return;
        if (!(obj1 != null && obj1.equals(obj2)))
            throw new ApiException(status, errorMsg, args);
    }
}
