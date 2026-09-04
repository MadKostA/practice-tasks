package org.example.spring_practice_tasks.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.enums.NoteExportFormat;
import org.example.spring_practice_tasks.impl.repo.NoteRepository;
import org.example.spring_practice_tasks.api.service.ExportService;
import org.example.spring_practice_tasks.impl.util.NoteMapper;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.handler.NoteExporter;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final Map<String, NoteExporter> exporters;
    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public ExportServiceImpl(Map<String, NoteExporter> exporters,
                             NoteRepository noteMapRepository,
                             NoteMapper noteMapper) {
        this.exporters = exporters;
        this.noteRepository = noteMapRepository;
        this.noteMapper = noteMapper;
    }

    @Override
    public byte[] exportNote(NoteExportFormat format) {
        log.info("Exporting all notes to '{}' format", format);

        NoteExporter noteExporter = exporters.get(format.name());

        List<Note> all = noteRepository.findAll();

        Collection<NoteModel> notes = all.stream()
                .map(noteMapper::toModel)
                .toList();

        byte[] convertedData = noteExporter.export(notes);

        log.info("All notes has been successfully converted to '{}' format", format);

        return convertedData;
    }
}
