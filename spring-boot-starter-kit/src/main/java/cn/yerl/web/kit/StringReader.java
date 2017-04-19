package cn.yerl.web.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alan on 2017/3/24.
 */
public class StringReader {
    public static String read(InputStream stream){
        if (stream == null){
            throw new IllegalArgumentException("参数[stream]不能为空");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null){
                result.append(line).append("\r\n");
            }
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }finally {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return result.toString();
    }
}
