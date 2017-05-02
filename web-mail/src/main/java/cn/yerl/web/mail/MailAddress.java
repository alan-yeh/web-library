package cn.yerl.web.mail;

/**
 * 邮箱地址
 * Created by alan on 2017/4/23.
 */
public class MailAddress {
    /**
     * 邮箱地址
     */
    private String address;
    /**
     * 邮箱名称
     */
    private String personal;

    public MailAddress(String address, String personal) {
        this.address = address;
        this.personal = personal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }
}
