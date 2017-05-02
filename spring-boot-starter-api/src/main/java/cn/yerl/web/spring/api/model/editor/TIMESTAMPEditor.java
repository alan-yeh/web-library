package cn.yerl.web.spring.api.model.editor;

import cn.yerl.web.kit.StrKit;
import oracle.sql.TIMESTAMP;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;

/**
 * Created by Alan Yeh on 2017/4/21.
 */
public class TIMESTAMPEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StrKit.isBlank(text)){
            setValue(null);
        }

        Long value = Long.parseLong(text);
        setValue(new TIMESTAMP(new Timestamp(value)));
    }
}
