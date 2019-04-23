package com.suneee.eas.common.component.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.suneee.eas.common.utils.DateUtil;

import java.io.IOException;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {
 
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException {
        String date = jp.getText();
        return DateUtil.getDate(date,DateUtil.FORMAT_DATE);
    }
}
