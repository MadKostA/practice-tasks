package org.example.spring_practice_tasks.impl.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CreatedNotesCounter {

    private final MeterRegistry meterRegistry;

    @Bean
    public Counter counter() {
        return Counter.builder("notes.created")
                .description("Total number of created notes")
                .register(meterRegistry);
    }
}
