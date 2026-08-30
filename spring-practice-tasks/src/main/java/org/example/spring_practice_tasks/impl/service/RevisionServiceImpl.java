package org.example.spring_practice_tasks.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.dto.PageResponseDto;
import org.example.spring_practice_tasks.api.dto.RevisionResponseDto;
import org.example.spring_practice_tasks.api.dto.SortResponseDto;
import org.example.spring_practice_tasks.api.service.RevisionService;
import org.example.spring_practice_tasks.impl.entity.NoteRevision;
import org.example.spring_practice_tasks.impl.repo.RevisionRepository;
import org.example.spring_practice_tasks.impl.util.RevisionMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('notes.admin')")
    public PageResponseDto getAllHistory(Pageable pageable) {
        log.info("Getting all notes history responsePage={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<NoteRevision> responsePage = revisionRepository.findAll(pageable);

        List<RevisionResponseDto> list = responsePage.get().map(revisionMapper::toResponseDto).toList();

        List<SortResponseDto> sortFieldsList = pageable.getSort()
                .stream().map(sort -> new SortResponseDto(sort.getProperty(), sort.getDirection().name()))
                .toList();

        return PageResponseDto.builder()
                .elements(list)
                .page(responsePage.getNumber())
                .size(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .sort(sortFieldsList)
                .build();
    }
}
