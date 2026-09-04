package org.example.spring_practice_tasks.impl.service.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.service.kafka.KafkaSender;
import org.example.spring_practice_tasks.impl.config.kafka.NotesTopicProperties;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@ConditionalOnProperty(
        prefix = "kafka.producer",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class NotesSender implements KafkaSender<UUID, NoteEvent> {

    private final NotesTopicProperties properties;
    private final KafkaTemplate<UUID, NoteEvent> kafkaTemplate;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendMessage(UUID messageId, NoteEvent noteEvent) {
        log.info("Kafka message sending to topic '{}', with message id '{}'", properties.getName(), messageId);
        kafkaTemplate.send(properties.getName(), messageId, noteEvent);
    }
}
