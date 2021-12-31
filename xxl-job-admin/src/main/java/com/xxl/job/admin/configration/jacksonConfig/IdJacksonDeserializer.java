package com.xxl.job.admin.configration.jacksonConfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * @Author lwk
 * @Date 2021/5/28 14:21
 * @Version 1.0
 * @Description
 */
public class IdJacksonDeserializer extends StdDeserializer<Id> {
    private static final long serialVersionUID = -4553122378393126795L;

    public IdJacksonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Id deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new Id(p.getValueAsLong());
    }
}


