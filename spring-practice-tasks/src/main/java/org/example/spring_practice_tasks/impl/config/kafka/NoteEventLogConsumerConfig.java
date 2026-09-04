package org.example.spring_practice_tasks.impl.config.kafka;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.UUID;

@Configuration
public class NoteEventLogConsumerConfig {

    private final KafkaProperties kafkaProperties;

    public NoteEventLogConsumerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public ConsumerFactory<UUID, NoteEvent> noteEventLogConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(),
                new ErrorHandlingDeserializer<>(new UUIDDeserializer()),
                new ErrorHandlingDeserializer<>(new JacksonJsonDeserializer<>(NoteEvent.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<UUID, NoteEvent> noteEventLogKafkaListenerContainerFactory(
            ConsumerFactory<UUID, NoteEvent> noteEventLogConsumerFactory, DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<UUID, NoteEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(noteEventLogConsumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterRecoverer(KafkaTemplate<UUID, NoteEvent> template) {
        return new DeadLetterPublishingRecoverer(template,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT",
                        record.partition()));
    }

    @Bean
    public DefaultErrorHandler errorHandler(DeadLetterPublishingRecoverer recoverer) {
        FixedBackOff backOff = new FixedBackOff(1000L, 2);

        return new DefaultErrorHandler(recoverer, backOff);
    }

}
