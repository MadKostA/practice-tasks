package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.api.repo.NoteMapRepository;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Deprecated
public class NoteMapMapRepositoryImpl implements NoteMapRepository {

    private final ConcurrentMap<UUID, NoteModel> NOTES_DB = new ConcurrentHashMap<>();

    @Override
    public UUID create(NoteModel noteModel) {
        UUID noteId = UUID.randomUUID();
        NOTES_DB.put(noteId, noteModel);

        return noteId;
    }

    @Override
    public NoteModel update(UUID id, NoteModel noteModel) {
        return NOTES_DB.computeIfPresent(id, (key, value) -> noteModel);
    }

    @Override
    public NoteModel get(UUID id) {
        return NOTES_DB.get(id);
    }

    @Override
    public Collection<NoteModel> getAll() {
        return NOTES_DB.values();
    }

    @Override
    public void delete(UUID id) {
        NOTES_DB.remove(id);
    }

    @Override
    public long getTotalNotesCount() {
        return NOTES_DB.size();
    }
}
