package cn.yerl.web.spring.sso.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * Token
 * Created by Alan Yeh on 2017/4/20.
 */
public class Token extends Model<Token> {
    public interface meta {
        String TABLE_NAME = "SSO_TOKEN";
        String AUTH_CODE = "AUTH_CODE";
        String USER_CODE = "USER_CODE";
    }

    public final static Token dao = new Token();
}
