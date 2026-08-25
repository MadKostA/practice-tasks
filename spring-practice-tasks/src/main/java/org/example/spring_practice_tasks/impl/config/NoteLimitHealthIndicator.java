package org.example.spring_practice_tasks.impl.config;

import org.example.spring_practice_tasks.api.service.NoteService;
import org.example.spring_practice_tasks.impl.config.notes_count.ProfileSettingsConfig;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class NoteLimitHealthIndicator implements HealthIndicator {

    private final NoteService noteService;
    private final ProfileSettingsConfig config;

    public NoteLimitHealthIndicator(NoteService noteService,
                                    ProfileSettingsConfig config) {
        this.noteService = noteService;
        this.config = config;
    }

    @Override
    public Health health() {
        long total = noteService.getTotalNotesCount();
        long limit = config.getCountLimit();

        if (total >= limit) {
            return Health.down()
                    .withDetail("reason", "Note limit exceeded")
                    .withDetail("totalNotes", total)
                    .withDetail("limit", limit)
                    .build();
        } else {
            return Health.up()
                    .withDetail("totalNotes", total)
                    .withDetail("limit", limit)
                    .build();
        }
    }
}