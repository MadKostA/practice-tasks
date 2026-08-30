package org.example.spring_practice_tasks.api.controller;

import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.AuthRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface AuthController {

    @PostMapping(UrlConstants.TOKEN_URL)
    ResponseEntity<Map<String, String>> token(@RequestBody AuthRequestDto authRequestDto);

}
