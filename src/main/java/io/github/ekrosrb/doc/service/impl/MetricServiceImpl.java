package io.github.ekrosrb.doc.service.impl;

import io.github.ekrosrb.doc.controller.model.metric.MetricCountResponse;
import io.github.ekrosrb.doc.repository.DocumentRepository;
import io.github.ekrosrb.doc.repository.model.DocumentKind;
import io.github.ekrosrb.doc.repository.model.DocumentRelationType;
import io.github.ekrosrb.doc.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

  private final DocumentRepository documentRepository;

  @Override
  @Transactional(readOnly = true)
  public List<MetricCountResponse> countByType(DocumentKind kind) {
    log.info("Counting entries per type for kind [{}]", kind);
    var kinds = kind == null ? EnumSet.allOf(DocumentKind.class) : EnumSet.of(kind);
    return documentRepository.countByType(kinds, DocumentRelationType.SUPERSEDES).stream()
        .map(count -> MetricCountResponse.builder()
            .type(count.getType())
            .count(count.getCount())
            .build())
        .toList();
  }

}
