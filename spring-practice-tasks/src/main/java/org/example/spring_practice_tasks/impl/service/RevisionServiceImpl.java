package org.example.spring_practice_tasks.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.dto.RevisionResponseDto;
import org.example.spring_practice_tasks.api.repo.RevisionRepository;
import org.example.spring_practice_tasks.api.service.RevisionService;
import org.example.spring_practice_tasks.impl.config.RevisionMapper;
import org.example.spring_practice_tasks.impl.entity.NoteRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RevisionServiceImpl implements RevisionService {

    private final RevisionMapper revisionMapper;
    private final RevisionRepository revisionRepository;

    public RevisionServiceImpl(RevisionMapper revisionMapper,
                               RevisionRepository revisionRepository) {
        this.revisionMapper = revisionMapper;
        this.revisionRepository = revisionRepository;
    }

    @Override
    public Page<RevisionResponseDto> getAllHistory(Pageable pageable) {
        log.info("Getting all notes history page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<NoteRevision> page = revisionRepository.findAll(pageable);

        List<RevisionResponseDto> list = page.get().map(revisionMapper::toResponseDto).toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
}
