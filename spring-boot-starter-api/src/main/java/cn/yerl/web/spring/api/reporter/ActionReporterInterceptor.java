package cn.yerl.web.spring.api.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * Action Report
 * Created by alan on 2016/11/26.
 */
public class ActionReporterInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(ActionReporterInterceptor.class);

    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod method = (HandlerMethod)handler;

            StringBuilder builder = new StringBuilder("Action report -------------- ").append(sdf.get().format(new Date())).append(" -------------------------\r\n");
            builder.append("Action      : ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\r\n");
            builder.append("Controller  : ").append(method.getBeanType().getName()).append(".(").append(method.getBeanType().getSimpleName()).append(".java:1)\r\n");
            builder.append("Method      : ").append(method.getMethod().getName()).append("\r\n");

            Enumeration<String> e = request.getParameterNames();
            if (e.hasMoreElements()){
                builder.append("Parameter   : ");
                while (e.hasMoreElements()){
                    String name = e.nextElement();
                    String[] values = request.getParameterValues(name);
                    if (values.length == 1) {
                        builder.append(name).append("=").append(values[0]);
                    }
                    else {
                        builder.append(name).append("[]={");
                        for (int i=0; i<values.length; i++) {
                            if (i > 0)
                                builder.append(",");
                            builder.append(values[i]);
                        }
                        builder.append("}");
                    }
                    builder.append("  ");
                }
                builder.append("\r\n");
            }
            builder.append("------------------------------------------------------------------------------");

            logger.info(builder.toString());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null && handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            StringBuilder builder = new StringBuilder("\nAction report -------------- ").append(sdf.get().format(new Date())).append(" -------------------------\r\n");
            builder.append("Action      : ").append(request.getMethod()).append(" ").append(request.getRequestURI()).append("\r\n");
            builder.append("Controller  : ").append(method.getBeanType().getName()).append(".(").append(method.getBeanType().getSimpleName()).append(".java:1)\r\n");
            builder.append("Method      : ").append(method.getMethod().getName()).append("\r\n");
            builder.append("Exception   : ").append("\r\n");


            StringWriter stringWriter = new StringWriter();

            PrintWriter writer = new PrintWriter(stringWriter);
            ex.printStackTrace(writer);

            builder.append(stringWriter.getBuffer());
            stringWriter.close();

            builder.append("\r\n------------------------------------------------------------------------------");
            logger.error(builder.toString());
        }
    }
}