package cn.yerl.web.spring.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Json Exception
 * Created by alan on 2016/11/19.
 */
public class JsonExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(JsonExceptionHandlerExceptionResolver.class);
    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        logger.error(exception.getMessage(), exception);

        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", new Date().getTime());

        if (exception instanceof ApiException){
            ApiException ex = (ApiException)exception;
            result.put("status", ex.getStatus());
            result.put("desc", ex.getErrorMsg());
        }else if ("org.springframework.security.access.AccessDeniedException".equals(exception.getClass().getName())) {
            result.put("status", ApiStatus.FORBIDDEN.getValue());
            result.put("desc", exception.getMessage());
        }else {
            result.put("status", ApiStatus.SERVER_ERROR.getValue());
            result.put("desc", exception.getMessage());
        }

        return new ModelAndView(new MappingJackson2JsonView(), result);
    }
}
