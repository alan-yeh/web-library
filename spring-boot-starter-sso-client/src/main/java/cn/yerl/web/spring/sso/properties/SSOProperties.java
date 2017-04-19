package cn.yerl.web.spring.sso.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.sso")
public class SSOProperties {
    private String applicationAddress;
    private String serverAddress;
    private String serverValidateAddress;
    private String urlPatterns;

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getApplicationAddress() {
        return applicationAddress;
    }

    public void setApplicationAddress(String applicationAddress) {
        this.applicationAddress = applicationAddress;
    }

    public String getServerValidateAddress() {
        return serverValidateAddress;
    }

    public void setServerValidateAddress(String serverValidateAddress) {
        this.serverValidateAddress = serverValidateAddress;
    }

    public String getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }
}
