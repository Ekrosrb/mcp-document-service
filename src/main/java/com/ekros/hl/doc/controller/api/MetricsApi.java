package com.ekros.hl.doc.controller.api;

import com.ekros.hl.doc.controller.model.metric.MetricCountResponse;
import com.ekros.hl.doc.repository.model.DocumentKind;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MetricsApi {

  List<MetricCountResponse> countByType(@RequestParam(required = false) DocumentKind kind);
}
