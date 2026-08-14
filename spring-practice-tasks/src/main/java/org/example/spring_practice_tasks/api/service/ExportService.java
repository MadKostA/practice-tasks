package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.enums.NoteExportFormat;

public interface ExportService {

    byte[] exportNote(NoteExportFormat format);

}
