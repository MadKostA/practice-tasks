package org.example.spring_practice_tasks.api.repo;

import org.example.spring_practice_tasks.impl.model.NoteModel;

import java.util.Collection;
import java.util.UUID;

public interface NoteMapRepository {

    UUID create(NoteModel noteModel);

    NoteModel update(UUID id, NoteModel noteDto);

    NoteModel get(UUID id);

    Collection<NoteModel> getAll();

    void delete(UUID id);

    long getTotalNotesCount();

}
