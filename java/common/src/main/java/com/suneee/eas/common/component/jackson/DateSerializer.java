package com.suneee.eas.common.component.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.suneee.eas.common.utils.DateUtil;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
 
    @Override
    public void serialize(Date dateTime, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(DateUtil.formatDate(dateTime,DateUtil.FORMAT_DATE));
    }
}
