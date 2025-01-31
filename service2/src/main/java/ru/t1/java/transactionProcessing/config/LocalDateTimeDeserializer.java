package ru.t1.java.transactionProcessing.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isArray() && node.size() >= 6) {
            return LocalDateTime.of(
                    node.get(0).asInt(),  // year
                    node.get(1).asInt(),  // month
                    node.get(2).asInt(),  // day
                    node.get(3).asInt(),  // hour
                    node.get(4).asInt(),  // minute
                    node.get(5).asInt()   // second
            );
        }

        throw new IOException("Invalid timestamp format: " + node.toString());
    }
}
