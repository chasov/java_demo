package ru.t1.java.demo.timeout_blocker.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import ru.t1.java.demo.timeout_blocker.dto.AcceptedTransactionDto;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class TransactionRateLimiterStream {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String INPUT_TOPIC = "t1_demo_transaction_accept";
    private static final String RESULT_TOPIC = "t1_demo_transaction_result";
    @Value("${account.block.time-window-ms}")
    private Duration TIME_WINDOW;

    @Value("${account.block.count}")
    private int MAX_TRANSACTIONS;

    @Bean
    public KafkaStreamsConfiguration streamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "transaction-rate-limiter");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092,localhost:39092,localhost:49092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(new HashMap<>()));
    }
    @Bean
    public StreamsBuilderFactoryBean streamsBuilder() {
        return new StreamsBuilderFactoryBean(streamsConfig());
    }

    @Bean
    public KStream<String, AcceptedTransactionDto> processStream(StreamsBuilderFactoryBean streamsBuilder) throws Exception {
        KStream<String, AcceptedTransactionDto> transactionsStream = streamsBuilder.getObject()
                .stream(INPUT_TOPIC, Consumed.with(Serdes.String(), AcceptedTransactionSerde.acceptedTransactionSerde()));

        // Применяем фильтрацию по сумме транзакции
        KStream<String, AcceptedTransactionDto> filteredStream = transactionsStream
                .filter((key, value) -> value.transactionAmount() > 1000);  // Фильтруем по сумме

        // Записываем результат в новый топик
        filteredStream.to("filtered-transactions", Produced.with(Serdes.String(), AcceptedTransactionSerde.acceptedTransactionSerde()));
        // Возвращаем поток для возможного использования в дальнейшем
        return filteredStream;
    }
    @Bean
    public NewTopic createFilteredTransactionTopic(){
        return TopicBuilder
                .name("filtered-transactions")
                .partitions(1)
                .replicas(1)
                .build();
    }

    private void createTransactionCountStream(StreamsBuilder builder) {
        KStream<String, String> transactions = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()));

        KTable<Windowed<String>, Long> counts = transactions
                .groupBy((key, value) -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(value);
                        return jsonNode.get("accountId").asText(); // Извлекаем accountId
                    } catch (Exception e) {
                        // В случае ошибки при парсинге бросаем ошибку
                        throw new RuntimeException( "Не прочитался accountId " + e.getMessage());
                    }
                }, Grouped.with(Serdes.String(), Serdes.String()))
                                .windowedBy(TimeWindows.ofSizeWithNoGrace(TIME_WINDOW))
                .count();

        counts.toStream()
                .filter((windowedKey, count) -> count > MAX_TRANSACTIONS)
                .map((windowedKey, count) -> KeyValue.pair(windowedKey.key(), "Too many transactions: " + count))
                .to(RESULT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }



}

