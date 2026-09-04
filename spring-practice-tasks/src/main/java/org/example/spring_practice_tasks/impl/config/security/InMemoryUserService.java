package org.example.spring_practice_tasks.impl.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public InMemoryUserService() {
        // user1: только чтение
        users.put("user1", build("user1", "password1", List.of("notes.read")));
        // user2: чтение + запись
        users.put("user2", build("user2", "password2", List.of("notes.read", "notes.write")));
        // admin: всё
        users.put("admin", build("admin", "admin123", List.of("notes.read", "notes.write", "notes.admin")));
    }

    private UserDetails build(String username, String password, List<String> authorities) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .authorities(simpleGrantedAuthorities)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = users.get(username);
        if (user == null) {
            log.error("Invalid username or password: username={}", username);
            throw new UsernameNotFoundException("Invalid username or password: username=" + username);
        }
        return user;
    }
}

