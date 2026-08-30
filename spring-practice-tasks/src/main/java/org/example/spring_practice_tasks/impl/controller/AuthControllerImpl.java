package org.example.spring_practice_tasks.impl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.controller.AuthController;
import org.example.spring_practice_tasks.api.dto.AuthRequestDto;
import org.example.spring_practice_tasks.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public ResponseEntity<Map<String, String>> token(@RequestBody AuthRequestDto authRequestDto) {
        log.info("Getting jwt token for user={}", authRequestDto.username());

        String jwtToken = authService.getJwtToken(authRequestDto);

        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}

