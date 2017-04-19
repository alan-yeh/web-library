package cn.yerl.web.kit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by alan on 2017/3/19.
 */
public class Render {
    private static ObjectMapper mapper = new ObjectMapper();

    public static void renderJson(Object result, HttpServletRequest request, HttpServletResponse response){
        String jsonText;
        try {
            jsonText = mapper.writeValueAsString(result);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

        PrintWriter writer = null;
        try {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            response.setContentType("application/json; charset=UTF-8");
            writer = response.getWriter();
            writer.write(jsonText);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (writer != null)
                writer.close();
        }
    }

    public static void renderRedirect(String redirect, Map<String, Object> queryParams, HttpServletRequest request, HttpServletResponse response){
        try {
            URIBuilder builder = new URIBuilder(redirect);

            if (queryParams != null){
                for (Map.Entry<String, Object> entry : queryParams.entrySet()){
                    builder.addParameter(entry.getKey(), entry.getValue().toString());
                }
            }
            response.sendRedirect(builder.build().toString());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}