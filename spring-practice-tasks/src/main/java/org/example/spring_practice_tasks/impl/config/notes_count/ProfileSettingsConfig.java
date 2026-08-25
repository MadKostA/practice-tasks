package org.example.spring_practice_tasks.impl.config.notes_count;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notes")
@Getter
@Setter
public class ProfileSettingsConfig {
    private int countLimit;
}