package org.example.spring_practice_tasks.impl.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kafka.topic.notes")
public class NotesTopicProperties {
    private String name;
    private int partitions;
    private int replicas;
}
