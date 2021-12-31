package com.xxl.job.admin.configration.jacksonConfig;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @Author lwk
 * @Date 2021/5/28 14:22
 * @Version 1.0
 * @Description
 */
public class IdJacksonSerializer extends StdSerializer<Id> {

    private static final long serialVersionUID = -4507747325609301322L;

    public IdJacksonSerializer(Class<Id> t) {
        super(t);
    }

    @Override
    public void serialize(Id value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(String.valueOf(value.getVal()));
    }
}
