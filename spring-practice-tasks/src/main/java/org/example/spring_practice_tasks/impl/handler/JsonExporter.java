package org.example.spring_practice_tasks.impl.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component("JSON")
public class JsonExporter implements NoteExporter {

    private final ObjectMapper objectMapper;

    public JsonExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] export(Collection<NoteModel> notes) {
        try {
            return objectMapper.writeValueAsString(notes)
                    .getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting to JSON", e);
        }
    }
}
