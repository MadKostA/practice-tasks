package org.example.spring_practice_tasks.impl.controller;

import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.config.AbstractIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NoteControllerIntegrationTest extends AbstractIntegrationTests {

    private static final UUID NOTE_ID = UUID.fromString("bb57298f-13ad-4ccb-8519-9c64e8288c0b");

    @Nested
    class Create {

        @Test
        @DisplayName("Должен вернуть 201 и ссылку на созданный объект")
        void createOk() throws Exception {
            // given
            NoteRequestDto requestDto = new NoteRequestDto("test", "test", "test1");
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
                    Arguments.of(named("Title is null", new NoteRequestDto(null, "test", "test1")),
                            "Заголовок заметки должен должен быть заполнен"),
                    Arguments.of(named("Title is blank", new NoteRequestDto(" ", "test", "test1")),
                            "Заголовок заметки должен должен быть заполнен"),
                    Arguments.of(named("Text is null", new NoteRequestDto("test", null, "test1")),
                            "Текст заметки должен должен быть заполнен"),
                    Arguments.of(named("Text is blank", new NoteRequestDto("test", " ", "test1")),
                            "Текст заметки должен должен быть заполнен"),
                    Arguments.of(named("Author is null", new NoteRequestDto("test", "test", null)),
                            "Автор заметки должен быть заполнен"),
                    Arguments.of(named("Author is blank", new NoteRequestDto("test", "test", " ")),
                            "Автор заметки должен быть заполнен")
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
                    new NoteRequestDto("test1", "test1", "test1"),
                    new NoteRequestDto("test2", "test2", "test1")
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
            NoteRequestDto requestDto = new NoteRequestDto("updated", "updated", "test1");
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
            NoteRequestDto requestDto = new NoteRequestDto("updated", "updated", "test1");
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
                            .param("size", "2"))
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