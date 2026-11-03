package com.ekros.hl.doc.service;

import com.ekros.hl.doc.controller.model.metric.MetricCountResponse;
import com.ekros.hl.doc.repository.model.DocumentKind;

import java.util.List;

public interface MetricService {

  List<MetricCountResponse> countByType(DocumentKind kind);

}
