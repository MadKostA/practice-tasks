package org.example.spring_practice_tasks.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.dto.AuthRequestDto;
import org.example.spring_practice_tasks.api.exceptions.IncorrectAuthorException;
import org.example.spring_practice_tasks.api.service.AuthService;
import org.example.spring_practice_tasks.impl.config.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String getJwtToken(AuthRequestDto authRequestDto) {
        var userDetails = userDetailsService.loadUserByUsername(authRequestDto.username());

        validatePassword(authRequestDto.password(), userDetails.getPassword());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtil.generateToken(authentication.getName(), authentication.getAuthorities());
    }

    private void validatePassword(String authPassword, String encodedPassword) {
        if (!passwordEncoder.matches(authPassword, encodedPassword)) {
            log.error("Bad auth attempt with incorrect password");
            throw new BadCredentialsException("Incorrect password");
        }
    }

    @Override
    public void checkAuthor(String author) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String currentUserName = auth.getName();

        Optional<? extends GrantedAuthority> first = auth.getAuthorities()
                .stream()
                .filter(authority -> authority.getAuthority().equals("notes.admin"))
                .findFirst();

        if (first.isEmpty() && !currentUserName.equals(author)) {
            log.error("Access denied to update note: author={}", currentUserName);
            throw new IncorrectAuthorException(currentUserName);
        }
    }

    @Override
    public String getCurrentAuthorName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    }
}
