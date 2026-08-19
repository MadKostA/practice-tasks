package org.example.spring_practice_tasks.impl.controller;

import org.example.spring_practice_tasks.api.controller.AppStatusController;
import org.example.spring_practice_tasks.api.dto.error.ResponseWrapperDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class AppStatusControllerImpl implements AppStatusController {

    public static final String RUNNING_STATUS = "RUNNING";

    @Override
    public ResponseEntity<ResponseWrapperDto> getStatus() {
        return ResponseEntity.ok(ResponseWrapperDto
                .builder()
                .response(RUNNING_STATUS)
                .time(Instant.now())
                .build());
    }
}
