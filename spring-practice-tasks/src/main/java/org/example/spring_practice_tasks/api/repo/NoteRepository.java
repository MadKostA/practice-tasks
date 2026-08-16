package org.example.spring_practice_tasks.api.repo;

import org.example.spring_practice_tasks.impl.model.NoteModel;

import java.util.Collection;

public interface NoteRepository {

    long create(NoteModel noteModel);

    NoteModel update(Long id, NoteModel noteDto);

    NoteModel get(Long id);

    Collection<NoteModel> getAll();

    void delete(Long id);

    long getTotalNotesCount();

}
