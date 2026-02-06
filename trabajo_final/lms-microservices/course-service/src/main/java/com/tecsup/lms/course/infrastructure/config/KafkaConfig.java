package com.tecsup.lms.course.infrastructure.config;

import com.tecsup.lms.shared.config.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic courseEventsTopic() {
        return TopicBuilder.name(KafkaTopics.COURSE_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic dlqCourseEventsTopic() {
        return TopicBuilder.name(KafkaTopics.DLQ_COURSE_EVENTS)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
