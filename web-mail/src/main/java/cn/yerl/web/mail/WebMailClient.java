package cn.yerl.web.mail;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Web Mail Client
 * Created by alan on 2017/4/23.
 */
public class WebMailClient {

    private final Session session;
    private final Properties props;

    public WebMailClient(Properties props){
        this.props = props;
        this.session = Session.getDefaultInstance(props);
    }

    private MailAddress from = null;
    public WebMailClient setForm(MailAddress address){
        from = address;
        return this;
    }
    public MailAddress getFrom(){
        return this.from;
    }


    public void send(WebMailRequest request) throws Exception{
        Transport transport = session.getTransport();
        transport.connect(props.getProperty("mail.smtp.user"), props.getProperty("mail.smtp.password"));

        MimeMessage message = new MimeMessage(session);

        if (request.getFrom() == null){
            if (this.from == null){
                throw new IllegalArgumentException("没有设置发件人");
            }else {
                message.setFrom(new InternetAddress(from.getAddress(), from.getPersonal(), "UTF-8"));
            }
        }else {
            message.setFrom(new InternetAddress(request.getFrom().getAddress(), request.getFrom().getPersonal(), "UTF-8"));
        }

        request.getTo().forEach(it -> {
            try {
                message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(it.getAddress(), it.getPersonal(), "UTF-8"));
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        });

        message.setSubject(request.getSubject());
        message.setContent(request.getContent(), "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();

        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.exmail.qq.com");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.user", "gitlab@codesync.cn");
        props.setProperty("mail.smtp.password", "Minstone123");

        WebMailClient client = new WebMailClient(props);

        WebMailRequest request = new WebMailRequest()
                .from(new MailAddress("gitlab@codesync.cn", "Archives"))
                .to(new MailAddress("yerl@minstone.com.cn", "叶瑞龙"))
                .withSubject("这是测试邮件")
                .withContent("<h1>Hello</h1>");

        client.send(request);
    }
}
