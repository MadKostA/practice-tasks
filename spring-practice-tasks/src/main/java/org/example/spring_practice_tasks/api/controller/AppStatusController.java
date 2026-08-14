package org.example.spring_practice_tasks.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import static org.example.spring_practice_tasks.api.constants.UrlConstants.PING_URL;

public interface AppStatusController {

    @GetMapping(value = PING_URL)
    ResponseEntity<?> getStatus();
}
