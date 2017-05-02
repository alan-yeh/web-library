package cn.yerl.web.spring.api.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import oracle.sql.TIMESTAMP;

import java.io.IOException;
import java.sql.SQLException;

/**
 * ORACLE TIMESTAMP Serializer
 * Created by Alan Yeh on 2017/4/20.
 */
public class TIMESTAMPSerializer extends JsonSerializer<TIMESTAMP> {
    @Override
    public void serialize(TIMESTAMP value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        try {
            gen.writeNumber(value.dateValue().getTime());
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        }
    }
}
