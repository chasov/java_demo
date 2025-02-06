package ru.t1.java.demo.timeout_blocker.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Serdes;
import ru.t1.java.demo.timeout_blocker.dto.AcceptedTransactionDto;

public class AcceptedTransactionSerde extends Serdes.WrapperSerde<AcceptedTransactionDto> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AcceptedTransactionSerde() {
        super(new Serializer<AcceptedTransactionDto>() {
            @Override
            public byte[] serialize(String topic, AcceptedTransactionDto data) {
                try {
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing AcceptedTransactionDto", e);
                }
            }
        }, new Deserializer<AcceptedTransactionDto>() {
            @Override
            public AcceptedTransactionDto deserialize(String topic, byte[] data) {
                try {
                    return objectMapper.readValue(data, AcceptedTransactionDto.class);
                } catch (Exception e) {
                    throw new RuntimeException("Error deserializing AcceptedTransactionDto", e);
                }
            }
        });
    }

    public static AcceptedTransactionSerde acceptedTransactionSerde() {
        return new AcceptedTransactionSerde();
    }
}

