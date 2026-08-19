package org.example.spring_practice_tasks.impl.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component("XML")
public class XmlExporter implements NoteExporter {

    private final ObjectMapper xmlMapper;

    public XmlExporter(ObjectMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @Override
    public byte[] export(Collection<NoteModel> notes) {
        try {
            return xmlMapper.writeValueAsString(notes)
                    .getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting to XML", e);
        }
    }
}
