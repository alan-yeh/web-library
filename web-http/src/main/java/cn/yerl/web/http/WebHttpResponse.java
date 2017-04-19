package cn.yerl.web.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by alan on 2016/11/9.
 */
public class WebHttpResponse {
    int status;
    String text;
    Header[] headers;

    WebHttpResponse(HttpResponse response) throws IOException{
        status = response.getStatusLine().getStatusCode();
        text = EntityUtils.toString(response.getEntity(), "UTF-8");
        headers = response.getAllHeaders();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }
}
