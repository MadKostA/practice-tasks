package org.example.spring_practice_tasks.impl.handler;

import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component("CSV")
public class CsvExporter implements NoteExporter {

    @Override
    public byte[] export(Collection<NoteModel> notes) {
        StringBuilder sb = new StringBuilder();
        sb.append("title;content;createdAt\n");
        for (NoteModel note : notes) {
            sb.append(note.title()).append(';')
                    .append(escapeCsv(note.text())).append(';')
                    .append(note.author()).append(';')
                    .append(note.createdAt()).append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        return value.replace(";", ",");
    }
}
