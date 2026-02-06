package com.tecsup.lms.payment.infrastructure.event;

import com.tecsup.lms.shared.config.KafkaTopics;
import com.tecsup.lms.shared.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public void publish(DomainEvent event) {
        log.info("Publishing event: {} [{}]", event.getEventType(), event.getEventId());

        String key = event.getAggregateId();

        kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS, key, event);

        log.info("Event published to topic: {}", KafkaTopics.PAYMENT_EVENTS);
    }
}
