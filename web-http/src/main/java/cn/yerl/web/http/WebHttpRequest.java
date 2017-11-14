package cn.yerl.web.http;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by alan on 2016/11/9.
 */
public class WebHttpRequest implements Serializable {
    String url;
    WebHttpMethod method;
    Map<String, Object> queryParams = new HashMap<String, Object>();
    Map<String, Object> pathParams = new HashMap<String, Object>();
    Map<String, Object> bodyParams = new HashMap<String, Object>();
    Map<String, Object> headers = new HashMap<String, Object>();
    private long timestamp = new Date().getTime();

    public long getTimestamp(){
        return timestamp;
    }
    public String getUrl(){
        return url;
    }

    private static WebHttpRequest create(WebHttpMethod method, String url){
        WebHttpRequest request = new WebHttpRequest();
        request.url = url;
        request.method = method;
        return request;
    }
    public WebHttpRequest(){}

    public WebHttpRequest(String httpMethod, String url){
        this(httpMethod);
        this.url = url;
    }

    public WebHttpRequest(String httpMethod){
        if (httpMethod.equalsIgnoreCase("GET")){
            this.method = WebHttpMethod.GET;
        }else if (httpMethod.equalsIgnoreCase("POST")){
            this.method = WebHttpMethod.POST;
        }else if (httpMethod.equalsIgnoreCase("DELETE")){
            this.method = WebHttpMethod.DELETE;
        }else if (httpMethod.equalsIgnoreCase("PUT")){
            this.method = WebHttpMethod.PUT;
        }else {
            throw new RuntimeException("Not Support Http Method [" + httpMethod + "]");
        }
    }

    public WebHttpRequest(HttpServletRequest request){
        this(request.getMethod());
        System.err.println("暂时只是使用RequestParameter获取，并未区分GET和POST请求的参数");

        String requestURI;
        try {
            URIBuilder builder = new URIBuilder();
            builder.setPath(request.getRequestURI());
            builder.setPort(request.getLocalPort());
            builder.setScheme(request.getScheme());
            builder.setHost(request.getLocalAddr());
            builder.setPath(request.getRequestURI());
            requestURI = builder.build().toString();
        }catch (Exception ex){
            throw new RuntimeException("Property [spring.sso.application-address] is not valid");
        }
        System.err.println("WebHttpRequest请求地址：" + requestURI);

        //暂时只支持GET，后面再将参数放进来
        this.url = requestURI;

        if (request.getMethod().equalsIgnoreCase("GET") || request.getMethod().equalsIgnoreCase("DELETE")){
            request.getQueryString();

            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()){
                String name = names.nextElement();
                this.withQueryParam(name, request.getParameter(name));
            }
        }else {
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()){
                String name = names.nextElement();
                this.withBodyParam(name, request.getParameter(name));
            }
        }
    }

    public WebHttpRequest copyHeader(HttpServletRequest request){
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()){
            String name = headers.nextElement();
            String value =request.getHeader(name);
            this.withHeader(name, value);
        }
        return this;
    }

    public static WebHttpRequest GET(String url){
        return create(WebHttpMethod.GET, url);
    }
    public static WebHttpRequest POST(String url){
        return create(WebHttpMethod.POST, url);
    }
    public static WebHttpRequest PUT(String url){
        return create(WebHttpMethod.PUT, url);
    }
    public static WebHttpRequest DELETE(String url){
        return create(WebHttpMethod.DELETE, url);
    }

    public WebHttpRequest withQueryParams(Map<String, Object> queryParams){
        this.queryParams.putAll(queryParams);
        return this;
    }

    public WebHttpRequest withQueryParam(String key, Object value){
        this.queryParams.put(key, value);
        return this;
    }

    public WebHttpRequest withPathParams(Map<String, Object> urlParams){
        this.pathParams.putAll(urlParams);
        return this;
    }

    public WebHttpRequest withPathParam(String key, Object value){
        this.pathParams.put(key, value);
        return this;
    }

    public WebHttpRequest withBodyParams(Map<String, Object> bodyParams){
        this.bodyParams.putAll(bodyParams);
        return this;
    }

    public WebHttpRequest withBodyParam(String key, Object value){
        this.bodyParams.put(key, value);
        return this;
    }

    public WebHttpRequest withHeaders(Map<String, Object> headers){
        this.headers.putAll(headers);
        return this;
    }

    public WebHttpRequest withHeader(String key, Object value){
        this.headers.put(key, value);
        return this;
    }
    private HttpUriRequest buildRequest(){
        try {
            HttpUriRequest request = null;

            URIBuilder queryBuilder = new URIBuilder(this.getUrl());

            for (Map.Entry<String, Object> entry : queryParams.entrySet()){
                queryBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }

            URI requestURI = queryBuilder.build();

            switch (this.method){
                case GET:
                    request = new HttpGet(requestURI);
                    break;
                case DELETE:
                    request = new HttpDelete(requestURI);
                    break;
                case POST:
                case PUT:
                    HttpEntityEnclosingRequestBase req = this.method == WebHttpMethod.POST ? new HttpPost(requestURI) : new HttpPut(requestURI);
                    List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                    for (Map.Entry<String, Object> entry : this.bodyParams.entrySet()){
                        formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
                    }
                    UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
                    req.setEntity(formEntity);
                    request = req;
            }
            for (Map.Entry<String, Object> entry : this.headers.entrySet()){
                if (entry.getKey() != null && entry.getValue() != null)
                    request.addHeader(entry.getKey(), entry.getValue().toString());
            }
            return request;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public WebHttpResponse execute(){
        try {

            HttpUriRequest request = buildRequest();

            HttpClient httpClient = getHttpsClient();

            HttpResponse response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();

            if (status > 300 && status < 400){
                Header[] redirectHeader = response.getHeaders("location");
                if (redirectHeader != null && redirectHeader.length > 0){
                    WebHttpRequest redirectRequest = WebHttpRequest.GET(redirectHeader[0].getValue());
                    redirectRequest.withHeaders(this.headers);
                    return redirectRequest.execute();
                }
            }

            return new WebHttpResponse(response);

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void rewrite(HttpServletResponse resp){
        HttpUriRequest request = buildRequest();

        long begin = new Date().getTime();

        try {
            HttpClient httpClient = getHttpsClient();

            HttpResponse response = httpClient.execute(request);

            int status = response.getStatusLine().getStatusCode();

            resp.setStatus(status);
            Header[] respHeaders = response.getAllHeaders();
            if (respHeaders != null){
                for (Header header : respHeaders){
                    //移除Transfer-Encoding:chunked属性
                    if (header.getName().equalsIgnoreCase("Transfer-Encoding"))
                        continue;
                    resp.setHeader(header.getName(), header.getValue());
                }
            }

            OutputStream stream = null;
            try {
                HttpEntity entity = response.getEntity();
                stream = resp.getOutputStream();
                entity.writeTo(stream);
                stream.flush();
            }finally {
                if (stream != null){
                    stream.close();
                }
            }
        }catch (Exception ex){
            throw new RuntimeException("请求地址发生错误：" + request.getURI().toString(), ex);
        }finally {
            long end = new Date().getTime();
            System.out.println("【请求地址】:" + request.getURI().toString());
            System.out.println("【共耗时】:" + (end - begin));
        }
    }

    static public HttpClient getHttpsClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }


    }
}
