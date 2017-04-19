package cn.yerl.web.spring.sso;

import cn.yerl.web.http.WebHttpRequest;
import cn.yerl.web.kit.Render;
import cn.yerl.web.spring.api.ApiResult;
import cn.yerl.web.spring.api.ApiStatus;
import cn.yerl.web.spring.sso.properties.SSOProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Configuration for SSO
 * Created by alan on 2017/3/13.
 */
@Configuration
@EnableConfigurationProperties(SSOProperties.class)
@Controller
@EnableScheduling
public class SSOClient extends WebSecurityConfigurerAdapter implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    private static Logger logger = LoggerFactory.getLogger(SSOClient.class);
    @Autowired
    SSOProperties properties;

    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    private Map<String, WebHttpRequest> returnUrls = new HashMap<>();

    /**
     * 60秒清空一次未使用的return url
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void clearReturnUrls(){
        List<String> willRemove = new ArrayList<>();
        long current = new Date().getTime();

        returnUrls.entrySet().forEach(entry ->{
            if (current - entry.getValue().getTimestamp() > 60 * 1000){
                willRemove.add(entry.getKey());
            }
        });

        willRemove.forEach(token -> returnUrls.remove(token));
        if (willRemove.size() > 0){
            logger.info("【清除未使用的token】本次共清除【" + willRemove.size() + "】个，剩余【" + returnUrls.size() + "】个");
        }
    }

    /**
     * 配置SSO拦截器
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(loginUrlAuthenticationEntryPoint())
                .accessDeniedPage("/api/security/login/failure")
                .and()
                .authorizeRequests().antMatchers("/api/disk/**","/api/message/push", "/api/security/validate", "/api/security/login/failure", "/").permitAll().anyRequest().authenticated()
                .and()
                .csrf().disable()
                //配置登录相关信息
                .formLogin()
                .loginProcessingUrl("/api/security/login").permitAll()
                .usernameParameter("token").passwordParameter("auth_code")
                .successHandler(this)
                .failureHandler(this)
                .and()
                //配置注消相关信息
                .logout().logoutUrl("/api/security/logout")
                .logoutSuccessUrl("/api/security/logout/success").permitAll();
    }


    /**
     * 验证token
     * Spring Security的login loginProcessingUrl必须使用post请求
     * 而SSO通过redirect返回回来必定是GET请求，所以使用这个方法进行代理
     */
    @GetMapping("/api/security/validate")
    public void validate(@RequestParam("token")String token,
                      @RequestParam("auth_code")String authCode,
                      HttpServletRequest request,
                      HttpServletResponse response) throws Exception{

        URIBuilder authUri = new URIBuilder();
        authUri.setScheme(request.getScheme());
        authUri.setHost(request.getLocalAddr());
        authUri.setPort(request.getLocalPort());
        authUri.setPath("/mobile-oa/api/security/login");

        WebHttpRequest authRequest = WebHttpRequest.POST(authUri.build().toString())
                .withBodyParam("token", token)
                .withBodyParam("auth_code", authCode);

        authRequest.copyHeader(request);

        try {
            authRequest.rewrite(response);
        }catch (Exception ex){
            logger.error("login rewrite error", ex);
        }
    }

    /**
     * 回写被拦截的请求的结果
     */
    @GetMapping("/api/security/rewrite")
    public void rewrite(@RequestParam("token") String token,
                        HttpServletRequest request,
                        HttpServletResponse response){

        WebHttpRequest returnReq = returnUrls.remove(token);

        returnReq.copyHeader(request);

        try {
            returnReq.rewrite(response);
        }catch (Exception ex){
            logger.error("rewrite error", ex);
        }
    }

    /**
     * 获取被登录时被拦截的请求, 并将其封装后保存到session中
     */
    public AuthenticationEntryPoint loginUrlAuthenticationEntryPoint() throws Exception{

        LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(""){
            @Override
            protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
                String token = UUID.randomUUID().toString();

                String loginUrl;
                try {
                    URIBuilder loginUri = new URIBuilder(properties.getServerAddress() + "/sso-server/authorize");

                    loginUri.addParameter("token", token);
                    loginUri.addParameter("request_uri", properties.getApplicationAddress() + "/api/security/validate");
                    loginUrl = loginUri.build().toString();
                }catch (Exception ex){
                    throw new RuntimeException(ex);
                }

                WebHttpRequest returnReq = new WebHttpRequest(request);
                returnUrls.put(token, returnReq);

                StringBuilder builder = new StringBuilder("Authentication report -------------- ").append(sdf.get().format(new Date())).append(" -----------------\r\n");
                builder.append("Redirect to authenticate address: ").append(loginUrl).append("\r\n");
                builder.append("Rewrite address: ").append(returnReq.getUrl()).append("\r\n");
                builder.append("------------------------------------------------------------------------------");
                logger.info(builder.toString());

                return loginUrl;
            }
        };
        return entryPoint;
    }

    /**
     * 处理登录成功，如果session中存有return_req，则重定向到/api/security/rewrite中，返回之前的结果
     * 之后以不在这个方法中返回之前的结果，是因为要将session_id的cookie写到response中
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String remoteUser = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //将Spring Security的Principal写到Session里，这样就可以通过注解获取当前登录的用户
        request.getSession().setAttribute("remoteUser", remoteUser);

        WebHttpRequest returnReq = returnUrls.get(request.getParameter("token"));

        StringBuilder builder = new StringBuilder("Authentication report -------------- ").append(sdf.get().format(new Date())).append(" -----------------\r\n");
        builder.append("Authentication Success: ").append(remoteUser).append("\r\n");
        if (returnReq != null){
            builder.append("Rewrite request:").append(returnReq.getUrl()).append("\r\n");
        }
        builder.append("------------------------------------------------------------------------------");
        logger.info(builder.toString());


        if (returnReq == null){
            Render.renderJson(new ApiResult<>(ApiStatus.OK, "登录成功", authentication.getPrincipal()), request, response);
        }else {
            String rewriteUrl = null;
            try {
                URIBuilder rewriteUri = new URIBuilder(properties.getApplicationAddress()+ "/api/security/rewrite");
                rewriteUri.addParameter("token", request.getParameter("token"));
                rewriteUrl = rewriteUri.build().toString();
            }catch (Exception ex){
                throw new ServletException("Can not build rewrite url", ex);
            }

            response.sendRedirect(rewriteUrl);
        }
    }

    /**
     * 处理登录失改
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        StringBuilder builder = new StringBuilder("Authentication report -------------- ").append(sdf.get().format(new Date())).append(" -----------------\r\n");
        builder.append("Authentication Failure").append("\r\n");
        builder.append("------------------------------------------------------------------------------");
        logger.info(builder.toString());

        Render.renderJson(ApiResult.failure(ApiStatus.UNAUTHORIZED, "用户名或密码错误"), request, response);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 配置authenticationProvider，从认验中心中获取session
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                StringBuilder builder = new StringBuilder("Authentication report -------------- ").append(sdf.get().format(new Date())).append(" -----------------\r\n");
                builder.append("Authentication: ").append("\r\n");
                builder.append("auth_code: ").append(authentication.getCredentials()).append("\r\n");
                builder.append("------------------------------------------------------------------------------");
                logger.info(builder.toString());

                if (authentication.isAuthenticated()){
                    return authentication;
                }
                try {

                    String responseText = WebHttpRequest.GET(properties.getServerValidateAddress() + "/sso-server/validate")
                            .withQueryParam("auth_code", authentication.getCredentials().toString())
                            .execute().getText();

                    JsonNode json = MAPPER.readTree(responseText);

                    if (json.get("status").asInt() == 200){
                        String remoteUser = json.get("data").get("user_code").asText();

                        // 验证成功，登录成功
                        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
                        Authentication result = new UsernamePasswordAuthenticationToken(remoteUser, authentication.getCredentials(), grantedAuthorities);

                        return result;
                    }
                    throw new BadCredentialsException(json.get("desc").asText());

                }catch (Exception ex){
                    throw new DisabledException("SSO不可用");
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.equals(authentication);
            }
        });
    }
}
