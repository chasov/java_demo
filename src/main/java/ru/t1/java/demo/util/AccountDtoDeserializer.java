package ru.t1.java.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.t1.java.demo.dto.AccountDto;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AccountDtoDeserializer extends JsonDeserializer<AccountDto> {
    @Override
    public AccountDto deserialize(String topic, Headers headers, ByteBuffer data) {
        try {
            String json = new String(data.array(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, AccountDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while deserializing AccountDto", e);
        }
    }

}
