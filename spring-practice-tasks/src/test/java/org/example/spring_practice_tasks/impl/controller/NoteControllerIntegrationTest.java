package org.example.spring_practice_tasks.impl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.spring_practice_tasks.api.repo.NoteMapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteMapRepository noteMapRepository;

    private final String BASE_URL = "/notes";

    // Очищаем репозиторий перед каждым тестом
    @BeforeEach
    void setUp() {
        // В репозитории нет метода очистки, поэтому удаляем все через итератор
        noteMapRepository.getAll().forEach(note -> {
            // Получаем id из модели? В модели нет id. Придётся хранить внешне.
            // В тестах мы будем создавать через репозиторий напрямую, поэтому знаем id.
        });
        // Но можно просто очистить через reflection или использовать другой подход.
        // Вместо этого создадим заметки через репозиторий в каждом тесте и запомним id.
        // Для простоты не будем очищать, а будем использовать уникальные данные.
    }

//    @Nested
//    class Create {
//
//        @Test
//        @DisplayName("Должен вернуть 201 и location, когда успешно создается заметка")
//        void createOk() throws Exception {
//            // given
//            NoteRequestDto request = new NoteRequestDto("Test Title", "Test Content");
//            String jsonRequest = objectMapper.writeValueAsString(request);
//
//            // when
//            ResultActions resultActions = mockMvc.perform(post(BASE_URL)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(jsonRequest));
//
//            // then
//            String location = resultActions.andExpect(status().isCreated())
//                    .andExpect(header().exists(HttpHeaders.LOCATION))
//                    .andReturn()
//                    .getResponse()
//                    .getHeader(HttpHeaders.LOCATION);
//
//            assertThat(location).matches("http://localhost/notes/\\d+");
//
//            long id = Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
//
//            NoteModel saved = noteMapRepository.get(id);
//
//            assertThat(saved).isNotNull();
//            assertThat(saved.title()).isEqualTo("Test Title");
//            assertThat(saved.content()).isEqualTo("Test Content");
//        }
//    }


//    // Тест получения существующей заметки
//    @Test
//    void getNote_shouldReturn200AndNote() throws Exception {
//        // Создаём заметку напрямую через репозиторий
//        NoteModel model = new NoteModel("Existing", "Content");
//        long id = noteRepository.create(model);
//
//        mockMvc.perform(get(baseUrl + "/{id}", id))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id))
//                .andExpect(jsonPath("$.title").value("Existing"))
//                .andExpect(jsonPath("$.content").value("Content"));
//    }
//
//    // Тест получения несуществующей заметки -> 404
//    @Test
//    void getNote_notFound_shouldReturn404() throws Exception {
//        long nonExistentId = 999L;
//        mockMvc.perform(get(baseUrl + "/{id}", nonExistentId))
//                .andExpect(status().isNotFound());
//    }
//
//    // Тест обновления существующей заметки
//    @Test
//    void updateNote_shouldReturn200AndUpdatedNote() throws Exception {
//        // Создаём
//        NoteModel model = new NoteModel("Old Title", "Old Content");
//        long id = noteRepository.create(model);
//
//        NoteRequestDto updateRequest = new NoteRequestDto("New Title", "New Content");
//        String jsonUpdate = objectMapper.writeValueAsString(updateRequest);
//
//        mockMvc.perform(put(baseUrl + "/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonUpdate))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(id))
//                .andExpect(jsonPath("$.title").value("New Title"))
//                .andExpect(jsonPath("$.content").value("New Content"));
//
//        // Проверяем, что в репозитории обновилось
//        NoteModel updated = noteRepository.get(id);
//        assertThat(updated.title()).isEqualTo("New Title");
//        assertThat(updated.content()).isEqualTo("New Content");
//    }
//
//    // Тест обновления несуществующей заметки -> 404
//    @Test
//    void updateNote_notFound_shouldReturn404() throws Exception {
//        long nonExistentId = 999L;
//        NoteRequestDto updateRequest = new NoteRequestDto("Any", "Any");
//        String jsonUpdate = objectMapper.writeValueAsString(updateRequest);
//
//        mockMvc.perform(put(baseUrl + "/{id}", nonExistentId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonUpdate))
//                .andExpect(status().isNotFound());
//    }
//
//    // Тест удаления существующей заметки
//    @Test
//    void deleteNote_shouldReturn204() throws Exception {
//        NoteModel model = new NoteModel("ToDelete", "Content");
//        long id = noteRepository.create(model);
//
//        mockMvc.perform(delete(baseUrl + "/{id}", id))
//                .andExpect(status().isNoContent());
//
//        // Проверяем, что заметка удалена
//        assertThat(noteRepository.get(id)).isNull();
//    }
//
//    // Тест удаления несуществующей заметки -> 204 (без ошибки)
//    @Test
//    void deleteNote_notFound_shouldReturn204() throws Exception {
//        long nonExistentId = 999L;
//        mockMvc.perform(delete(baseUrl + "/{id}", nonExistentId))
//                .andExpect(status().isNoContent());
//    }
//
//    // Тест экспорта в JSON
//    @Test
//    void exportNotes_json_shouldReturnFile() throws Exception {
//        // Создаём несколько заметок
//        noteRepository.create(new NoteModel("Note1", "Content1"));
//        noteRepository.create(new NoteModel("Note2", "Content2"));
//
//        mockMvc.perform(get(baseUrl + "/export")
//                        .param("format", "json"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=notes.json"))
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].title").value("Note1"))
//                .andExpect(jsonPath("$[1].title").value("Note2"));
//    }
//
//    // Тест экспорта в XML
//    @Test
//    void exportNotes_xml_shouldReturnFile() throws Exception {
//        noteRepository.create(new NoteModel("Note1", "Content1"));
//
//        mockMvc.perform(get(baseUrl + "/export")
//                        .param("format", "xml"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_XML))
//                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=notes.xml"));
//        // Можно проверить структуру XML, но для простоты ограничимся заголовками
//    }
//
//    // Тест экспорта в CSV
//    @Test
//    void exportNotes_csv_shouldReturnFile() throws Exception {
//        noteRepository.create(new NoteModel("Note1", "Content1"));
//
//        mockMvc.perform(get(baseUrl + "/export")
//                        .param("format", "csv"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
//                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=notes.csv"));
//    }
//
//    // Тест экспорта с неверным форматом -> 400
//    @Test
//    void exportNotes_invalidFormat_shouldReturn400() throws Exception {
//        mockMvc.perform(get(baseUrl + "/export")
//                        .param("format", "pdf"))
//                .andExpect(status().isBadRequest());
//    }
//
//    // ====== Тестовая конфигурация для недостающих бинов ======
//    @TestConfiguration
//    static class TestConfig {
//
//        // Заглушка для лимит-чекера (всегда разрешает)
//        @Bean
//        @Primary
//        public NotesLimitChecker testNotesLimitChecker() {
//            return total -> {
//                // Никаких ограничений
//            };
//        }
//
//        // Заглушки для экспортёров
//        @Bean
//        public NoteExporter jsonExporter() {
//            return new NoteExporter() {
//                @Override
//                public byte[] export(Collection<NoteModel> notes) {
//                    // Возвращаем простой JSON
//                    StringBuilder sb = new StringBuilder("[");
//                    int i = 0;
//                    for (NoteModel note : notes) {
//                        if (i++ > 0) sb.append(",");
//                        sb.append("{\"title\":\"").append(note.title())
//                                .append("\",\"content\":\"").append(note.content()).append("\"}");
//                    }
//                    sb.append("]");
//                    return sb.toString().getBytes();
//                }
//            };
//        }
//
//        @Bean
//        public NoteExporter xmlExporter() {
//            return new NoteExporter() {
//                @Override
//                public byte[] export(Collection<NoteModel> notes) {
//                    StringBuilder sb = new StringBuilder("<notes>");
//                    for (NoteModel note : notes) {
//                        sb.append("<note><title>").append(note.title())
//                                .append("</title><content>").append(note.content())
//                                .append("</content></note>");
//                    }
//                    sb.append("</notes>");
//                    return sb.toString().getBytes();
//                }
//            };
//        }
//
//        @Bean
//        public NoteExporter csvExporter() {
//            return new NoteExporter() {
//                @Override
//                public byte[] export(Collection<NoteModel> notes) {
//                    StringBuilder sb = new StringBuilder("title,content\n");
//                    for (NoteModel note : notes) {
//                        sb.append(note.title()).append(",").append(note.content()).append("\n");
//                    }
//                    return sb.toString().getBytes();
//                }
//            };
//        }
//
//        // Регистрируем их в Map, которую инжектит сервис
//        @Bean
//        public Map<String, NoteExporter> exporters(NoteExporter jsonExporter,
//                                                   NoteExporter xmlExporter,
//                                                   NoteExporter csvExporter) {
//            Map<String, NoteExporter> map = new HashMap<>();
//            map.put(NoteExportFormat.JSON.name(), jsonExporter);
//            map.put(NoteExportFormat.XML.name(), xmlExporter);
//            map.put(NoteExportFormat.CSV.name(), csvExporter);
//            return map;
//        }
//    }
}