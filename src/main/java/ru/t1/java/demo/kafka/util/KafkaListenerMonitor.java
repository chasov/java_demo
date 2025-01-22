package ru.t1.java.demo.kafka.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListenerMonitor {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @EventListener(ContextRefreshedEvent.class)
    public void checkListenerStatus() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            log.info("Kafka Listener {} is running", container.getListenerId());
        });
    }
}
