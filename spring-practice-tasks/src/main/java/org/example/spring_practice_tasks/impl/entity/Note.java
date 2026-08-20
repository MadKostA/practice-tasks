package org.example.spring_practice_tasks.impl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notes")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "author")
    private String author;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "optlock")
    private int version;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
