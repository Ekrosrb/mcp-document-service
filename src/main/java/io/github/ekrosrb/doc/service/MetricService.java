package io.github.ekrosrb.doc.service;

import io.github.ekrosrb.doc.controller.model.metric.MetricCountResponse;
import io.github.ekrosrb.doc.repository.model.DocumentKind;

import java.util.List;

public interface MetricService {

  List<MetricCountResponse> countByType(DocumentKind kind);

}
