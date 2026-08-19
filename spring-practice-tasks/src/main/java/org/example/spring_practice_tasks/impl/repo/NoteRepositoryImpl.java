package org.example.spring_practice_tasks.impl.repo;

import org.example.spring_practice_tasks.api.repo.NoteRepository;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NoteRepositoryImpl implements NoteRepository {

    private final ConcurrentMap<Long, NoteModel> NOTES_DB = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong(0);

    @Override
    public long create(NoteModel noteModel) {
        long noteId = id.incrementAndGet();
        NOTES_DB.put(noteId, noteModel);

        return noteId;
    }

    @Override
    public NoteModel update(Long id, NoteModel noteModel) {
        return NOTES_DB.computeIfPresent(id, (key, value) -> noteModel);
    }

    @Override
    public NoteModel get(Long id) {
        return NOTES_DB.get(id);
    }

    @Override
    public Collection<NoteModel> getAll() {
        return NOTES_DB.values();
    }

    @Override
    public void delete(Long id) {
        NOTES_DB.remove(id);
    }

    @Override
    public long getTotalNotesCount() {
        return NOTES_DB.size();
    }
}
