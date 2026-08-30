package org.example.spring_practice_tasks.kafka;

import org.awaitility.Awaitility;
import org.example.spring_practice_tasks.AbstractKafkaIntegrationTests;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.AuthRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.service.AuthService;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.entity.NoteEventLog;
import org.example.spring_practice_tasks.impl.model.EventType;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.example.spring_practice_tasks.impl.repo.NoteEventLogRepository;
import org.example.spring_practice_tasks.impl.repo.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class NoteEventLogIntegrationTest extends AbstractKafkaIntegrationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NoteEventLogRepository noteEventLogRepository;

    @Autowired
    private KafkaTemplate<UUID, NoteEvent> kafkaTemplate;

    @Value("${kafka.topic.notes.name}")
    private String NOTE_TOPIC;

    @Value("${kafka.topic.notes-dlt.name}")
    private String NOTE_TOPIC_DLT;

    @Nested
    class Create {

        @Test
        @DisplayName("""
                Должен создать запись в note_entity_log через отправку сообщения в кафку,
                а при повторной отправке сообщения с тем же eventId ничего не сделать""")
        @Transactional
        void shouldCreateNoteAndLogEventAndBeIdempotent() throws Exception {
            // given
            AuthRequestDto authRequestDto = new AuthRequestDto("user2", "password2");
            String jwtToken = authService.getJwtToken(authRequestDto);

            NoteRequestDto noteRequestDto = new NoteRequestDto("Test Title", "Test Text");
            String requestJson = objectMapper.writeValueAsString(noteRequestDto);

            // when
            mockMvc.perform(post(UrlConstants.NOTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isCreated());

            // then
            List<Note> notes = noteRepository.findAll();
            assertThat(notes).hasSize(1);
            Note savedNote = notes.get(0);
            assertThat(savedNote)
                    .isNotNull();
            assertThat(savedNote.getTitle())
                    .isEqualTo(noteRequestDto.title());


            // Ожидание появления записи в note_event_log
            UUID noteId = savedNote.getId();
            Awaitility.await()
                    .atMost(Duration.ofSeconds(10))
                    .pollInterval(Duration.ofMillis(100))
                    .until(() -> {
                        List<NoteEventLog> logs = noteEventLogRepository.findAllByNoteId(noteId);
                        return !logs.isEmpty();
                    });

            List<NoteEventLog> logs = noteEventLogRepository.findAllByNoteId(noteId);
            assertThat(logs).hasSize(1);
            NoteEventLog logEntry = logs.get(0);
            assertThat(logEntry.getEventType())
                    .isEqualTo(EventType.CREATED.name());

            // Повторная отправка того же события
            UUID eventId = logEntry.getEventId();
            NoteEvent duplicateEvent = NoteEvent.builder()
                    .eventId(eventId)
                    .noteId(noteId)
                    .author(savedNote.getAuthor())
                    .eventType(EventType.CREATED)
                    .occurredAt(logEntry.getOccurredAt())
                    .build();

            kafkaTemplate.send(NOTE_TOPIC, noteId, duplicateEvent);

            Awaitility.await()
                    .atMost(Duration.ofSeconds(5))
                    .pollInterval(Duration.ofMillis(200))
                    .until(() -> noteEventLogRepository.findAllByNoteId(noteId).size() == 1);

            assertThat(noteRepository.findAll()).hasSize(1);

            assertThat(noteEventLogRepository.findAllByNoteId(noteId)).hasSize(1);
        }
    }


}