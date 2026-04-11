package com.project.shopapp.exceptions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        // Đọc mảng giá trị từ JSON
        int[] dateTimeValues = jsonParser.readValueAs(int[].class);

        // Tạo đối tượng LocalDateTime từ mảng giá trị
        LocalDateTime localDateTime = LocalDateTime.of(
                dateTimeValues[0],  // Năm
                dateTimeValues[1],  // Tháng
                dateTimeValues[2],  // Ngày
                dateTimeValues[3],  // Giờ
                dateTimeValues[4],  // Phút
                dateTimeValues[5],  // Giây
                dateTimeValues[6]   // Nano giây
        );

        return localDateTime;
    }
}

