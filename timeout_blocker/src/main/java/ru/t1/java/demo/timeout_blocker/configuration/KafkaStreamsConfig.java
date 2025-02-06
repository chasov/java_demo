package ru.t1.java.demo.timeout_blocker.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.t1.java.demo.timeout_blocker.dto.AcceptedTransactionDto;
import ru.t1.java.demo.timeout_blocker.dto.TransactionResult;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private final int N = 5; // Количество транзакций для блокировки
    private final Duration T = Duration.ofSeconds(5); // Временное окно

    @Bean
    public StreamsBuilder builder(){
        return new StreamsBuilder();
    }

    @Bean
    public KStream<Long, TransactionResult> kafkaStream(StreamsBuilder builder) {
        KStream<Long, AcceptedTransactionDto> stream = builder.stream("t1_demo_transactions_accept");

        KTable<Windowed<Long>, Long> transactionCounts = stream
                .groupBy((key, transaction) -> transaction.accountId())
                .windowedBy(TimeWindows.ofSizeWithNoGrace(T))
                .count();

        KStream<Long, TransactionResult> blockedTransactions = transactionCounts
                .toStream()
                .filter((windowedKey, count) -> count >= N)
                .flatMap((windowedKey, count) -> {
                    List<KeyValue<Long, TransactionResult>> results = new ArrayList<>();
                    results.add(new KeyValue<>(windowedKey.key(), new TransactionResult(null, windowedKey.key(), "BLOCKED")));
                    return results;
                });

        KStream<Long, TransactionResult> rejectedTransactions = stream
                .filter((key, transaction) -> transaction.transactionAmount() > transaction.accountBalance())
                .map((key, transaction) -> KeyValue.pair(transaction.accountId(), new TransactionResult(transaction.transactionId(), transaction.accountId(), "REJECTED")));

        KStream<Long, TransactionResult> acceptedTransactions = stream
                .filter((key, transaction) -> transaction.transactionAmount() <= transaction.accountBalance())
                .map((key, transaction) -> KeyValue.pair(transaction.accountId(), new TransactionResult(transaction.transactionId(), transaction.accountId(), "ACCEPTED")));

        KStream<Long, TransactionResult> finalStream = blockedTransactions
                .merge(rejectedTransactions)
                .merge(acceptedTransactions);

        finalStream.to("t1_demo_transaction_result", Produced.with(Serdes.Long(), new JsonSerde<>(TransactionResult.class)));

        return finalStream;
    }
}
