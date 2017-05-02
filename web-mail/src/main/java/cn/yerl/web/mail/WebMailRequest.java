package cn.yerl.web.mail;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Web Mail Request
 * Created by alan on 2017/4/23.
 */
public class WebMailRequest {
    private MailAddress from;
    private List<MailAddress> to;
    private String subject;
    private String content;

    public WebMailRequest(){
        this.to = new ArrayList<>();
    }

    public WebMailRequest from(MailAddress address){
        this.from = address;
        return this;
    }

    public WebMailRequest to(MailAddress... addresses){
        this.to.addAll(Arrays.asList(addresses));
        return this;
    }

    public WebMailRequest withSubject(String subject){
        this.subject = subject;
        return this;
    }

    public WebMailRequest withContent(String content){
        this.content = content;
        return this;
    }


    public MailAddress getFrom() {
        return from;
    }

    public void setFrom(MailAddress from) {
        this.from = from;
    }

    public List<MailAddress> getTo() {
        return to;
    }

    public void setTo(List<MailAddress> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
