package org.example.spring_practice_tasks.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.enums.NoteExportFormat;
import org.example.spring_practice_tasks.api.repo.NoteRepository;
import org.example.spring_practice_tasks.api.service.ExportService;
import org.example.spring_practice_tasks.impl.handler.NoteExporter;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final Map<String, NoteExporter> exporters;
    private final NoteRepository noteRepository;

    public ExportServiceImpl(Map<String, NoteExporter> exporters, NoteRepository noteRepository) {
        this.exporters = exporters;
        this.noteRepository = noteRepository;
    }

    @Override
    public byte[] exportNote(NoteExportFormat format) {
        log.info("Exporting all notes to '{}' format", format);

        NoteExporter noteExporter = exporters.get(format.name());

        Collection<NoteModel> notes = noteRepository.getAll();

        byte[] convertedData = noteExporter.export(notes);

        log.info("All notes has been successfully converted to '{}' format", format);

        return convertedData;
    }
}
