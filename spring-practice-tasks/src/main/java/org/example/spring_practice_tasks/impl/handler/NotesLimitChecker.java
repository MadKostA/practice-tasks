package org.example.spring_practice_tasks.impl.handler;

import org.example.spring_practice_tasks.api.exceptions.NotesCountLimitException;
import org.example.spring_practice_tasks.impl.config.notes_count.ProfileSettingsConfig;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class NotesLimitChecker {

    private final ProfileSettingsConfig config;
    private final Environment environment;

    public NotesLimitChecker(ProfileSettingsConfig config, Environment environment) {
        this.config = config;
        this.environment = environment;
    }

    public void checkNotesLimit(long totalNotesCount) {
        if (environment.acceptsProfiles(Profiles.of("dev"))
                && config.getCountLimit() <= totalNotesCount) {

            throw new NotesCountLimitException(config.getCountLimit());
        }
    }
}
