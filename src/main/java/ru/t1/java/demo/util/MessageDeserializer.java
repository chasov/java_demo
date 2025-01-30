package ru.t1.java.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.ClientDto;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDeserializer<T> extends JsonDeserializer<T> {

    private final ObjectMapper objectMapper;

    public MessageDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    private static String getMessage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            objectMapper.readValue(getMessage(data), ClientDto.class);
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.warn("Произошла ошибка во время десериализации сообщения {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.warn("Произошла ошибка во время десериализации сообщения {}",
                    new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }

}
