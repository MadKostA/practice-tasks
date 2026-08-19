package org.example.spring_practice_tasks.impl.handler;

import org.example.spring_practice_tasks.impl.model.NoteModel;

import java.util.Collection;

public interface NoteExporter {
    byte[] export(Collection<NoteModel> notes);
}
