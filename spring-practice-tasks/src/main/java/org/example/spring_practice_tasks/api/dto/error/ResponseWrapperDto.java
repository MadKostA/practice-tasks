package org.example.spring_practice_tasks.api.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseWrapperDto(
        Object response,
        Instant time
) {

}
