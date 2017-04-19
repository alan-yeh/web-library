package cn.yerl.web.spring.api;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Json Exception
 * Created by alan on 2016/11/19.
 */
public class JsonExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        ApiResult result;
        if (exception instanceof ApiException){
            ApiException ex = (ApiException)exception;
            result = ApiResult.failure(ex.getStatus(), ex.getErrorMsg());
        }else {
            result = ApiResult.failure(ApiStatus.SERVER_ERROR, exception.getMessage());
        }

        MappingJackson2JsonView view = new MappingJackson2JsonView();

        ModelAndView mv = new ModelAndView(view);

        mv.addObject(result);

        return mv;
    }
}
