package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.AuthRequestDto;

public interface AuthService {

    String getJwtToken(AuthRequestDto authRequestDto);

    void checkAuthor(String author);

    String getCurrentAuthorName();

}
