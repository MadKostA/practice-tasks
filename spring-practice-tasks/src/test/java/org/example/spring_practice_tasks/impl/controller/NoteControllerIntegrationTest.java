package org.example.spring_practice_tasks.impl.controller;

import org.example.spring_practice_tasks.AbstractIntegrationTests;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.repo.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NoteControllerIntegrationTest extends AbstractIntegrationTests {

    @Autowired
    private NoteRepository noteRepository;

    private static final UUID NOTE_ID = UUID.fromString("bb57298f-13ad-4ccb-8519-9c64e8288c0b");

    @Nested
    class Create {

        @Test
        @DisplayName("Должен вернуть 201 и ссылку на созданный объект")
        void createOk() throws Exception {
            // given
            NoteRequestDto requestDto = new NoteRequestDto("test", "test");
            String requestJson = objectMapper.writeValueAsString(requestDto);
            String responseLocation = "http://localhost/notes/";

            // when
            ResultActions result = mockMvc.perform(post(UrlConstants.NOTE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson));

            // then
            String location = result.andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION))
                    .andReturn()
                    .getResponse()
                    .getHeader(HttpHeaders.LOCATION);

            assertThat(location).contains(responseLocation);
        }

        @ParameterizedTest
        @MethodSource(value = "createNotValidDataSource")
        @DisplayName("Должен вернуть ошибку, когда входной json содержит невалидные данные")
        void createNotValidData(NoteRequestDto requestDto, String errorMessage) throws Exception {
            // given
            String requestJson = objectMapper.writeValueAsString(requestDto);

            // when
            ResultActions result = mockMvc.perform(post(UrlConstants.NOTE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andDo(print());

            // then
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].message").value(errorMessage));
        }

        public static Stream<Arguments> createNotValidDataSource() {
            return Stream.of(
                    Arguments.of(named("Title is null", new NoteRequestDto(null, "test")),
                            "Заголовок заметки должен должен быть заполнен"),
                    Arguments.of(named("Title is blank", new NoteRequestDto(" ", "test")),
                            "Заголовок заметки должен должен быть заполнен"),
                    Arguments.of(named("Text is null", new NoteRequestDto("test", null)),
                            "Текст заметки должен должен быть заполнен"),
                    Arguments.of(named("Text is blank", new NoteRequestDto("test", " ")),
                            "Текст заметки должен должен быть заполнен")
            );
        }
    }

    @Nested
    class CreateBatch {

        @Test
        @DisplayName("Должен вернуть 200 при успешном пакетном создании заметок")
        void createOk() throws Exception {
            // given
            List<NoteRequestDto> requestDtos = List.of(
                    new NoteRequestDto("test1", "test1"),
                    new NoteRequestDto("test2", "test2")
            );
            String requestJson = objectMapper.writeValueAsString(requestDtos);

            // when
            ResultActions result = mockMvc.perform(post(UrlConstants.NOTE_BATCH_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andDo(print());

            // then
            result.andExpect(status().isOk());
        }

    }

    @Nested
    class Update {

        @Test
        @DisplayName("Должен обновить существующую заметку")
        @Sql("/sql/insert-note.sql")
        void updateOk() throws Exception {
            // given
            NoteRequestDto requestDto = new NoteRequestDto("updated", "updated");
            String requestJson = objectMapper.writeValueAsString(requestDto);
            String responseJson = resourceUtils.getJsonFromResources("json/notes/update-note-response-dto.json",
                    NoteResponseDto.class);

            // when
            ResultActions result = mockMvc.perform(put(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andDo(print());

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().json(responseJson));
        }

        @Test
        @DisplayName("Должен вернуть ошибку, когда не существует заметки с указанным id")
        void updateNotFound() throws Exception {
            // given
            NoteRequestDto requestDto = new NoteRequestDto("updated", "updated");
            String requestJson = objectMapper.writeValueAsString(requestDto);
            String errorMessage = "Не найдена заметка с id=bb57298f-13ad-4ccb-8519-9c64e8288c0b";

            // when
            ResultActions result = mockMvc.perform(put(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andDo(print());

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @RepeatedTest(value = 10)
        @DisplayName("Должен в одном потоке обновить существующую заметку, а в другом потоке выбрасить OptimisticLockingFailureException")
        @Sql("/sql/insert-note.sql")
        void throwOptimisticLocking() throws Exception {
            // given
            String errorMessage = "Unexpected row count (expected row count 1 but was 0)";
            ExecutorService executor = Executors.newFixedThreadPool(2);
            CyclicBarrier barrier = new CyclicBarrier(2);

            NoteRequestDto requestDtoThread1 = new NoteRequestDto("thread 1", "thread 1");
            String requestJsonThread1 = objectMapper.writeValueAsString(requestDtoThread1);

            NoteRequestDto requestDtoThread2 = new NoteRequestDto("thread 2", "thread 2");
            String requestJsonThread2 = objectMapper.writeValueAsString(requestDtoThread2);

            AtomicReference<ResultActions> resultActions1 = new AtomicReference<>();
            AtomicReference<ResultActions> resultActions2 = new AtomicReference<>();

            // when
            executor.submit(() -> {
                try {
                    barrier.await();
                    resultActions1.set(
                            mockMvc.perform(put(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJsonThread1)));
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            executor.submit(() -> {
                try {
                    barrier.await();
                    resultActions2.set(
                            mockMvc.perform(put(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJsonThread2))
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // then
            if (resultActions1.get().andReturn().getResolvedException() != null) {
                Exception resolvedException = resultActions1.get().andReturn().getResolvedException();
                assertThat(resolvedException.getClass())
                        .isEqualTo(ObjectOptimisticLockingFailureException.class);
                assertThat(resolvedException.getMessage())
                        .contains(errorMessage);
            } else if (resultActions2.get().andReturn().getResolvedException() != null) {
                Exception resolvedException = resultActions2.get().andReturn().getResolvedException();
                assertThat(resolvedException.getClass())
                        .isEqualTo(ObjectOptimisticLockingFailureException.class);
                assertThat(resolvedException.getMessage())
                        .contains(errorMessage);
            }

            Note updatedNote = noteRepository.findById(NOTE_ID).orElseThrow();
            assertThat(updatedNote.getVersion()).isEqualTo(1);
            assertThat(updatedNote.getTitle()).contains("thread");
            assertThat(updatedNote.getText()).contains("thread");
        }

    }

    @Nested
    class Get {

        @Test
        @DisplayName("Должен вернуть вернуть существующую заметку по id")
        @Sql("/sql/insert-note.sql")
        void getOk() throws Exception {
            // given
            String responseJson = resourceUtils.getJsonFromResources("json/notes/get-note-response-dto.json",
                    NoteResponseDto.class);

            // when
            ResultActions result = mockMvc.perform(get(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID))
                    .andDo(print());

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().json(responseJson));
        }

        @Test
        @DisplayName("Должен вернуть 404 и ошибку, когда не существует заметки с указанным id")
        void getNotFound() throws Exception {
            // given
            String errorMessage = "Не найдена заметка с id=bb57298f-13ad-4ccb-8519-9c64e8288c0b";

            // when
            ResultActions result = mockMvc.perform(get(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID))
                    .andDo(print());

            // then
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

    }

    @Nested
    class GetAllHistory {

        @Test
        @DisplayName("Должен вернуть историю редактирования всех заметок с пагинацией")
        @Sql(value = {"/sql/insert-note.sql", "/sql/insert-revisions.sql"})
        void getAllHistoryOk() throws Exception {
            // given
            String responseJson
                    = resourceUtils.getStringFromResources("json/revisions/get-all-history-response-dto.json");

            // when
            ResultActions result = mockMvc.perform(get(UrlConstants.NOTE_HISTORY_URL)
                            .param("page", "0")
                            .param("size", "2")
                            .param("sort", "changedAt,asc"))
                    .andDo(print());

            // then
            result.andExpect(status().isOk())
                    .andExpect(content().json(responseJson));
        }

    }

    @Nested
    class Delete {
        @Test
        @DisplayName("Должен вернуть 201 и ссылку на созданный объект")
        @Sql("/sql/insert-note.sql")
        void deleteOk() throws Exception {
            // given
            // when
            ResultActions result = mockMvc.perform(delete(UrlConstants.NOTE_WITH_ID_URL, NOTE_ID))
                    .andDo(print());

            // then
            result.andExpect(status().isNoContent());
        }
    }

}