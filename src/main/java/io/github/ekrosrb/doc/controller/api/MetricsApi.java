package io.github.ekrosrb.doc.controller.api;

import io.github.ekrosrb.doc.controller.model.metric.MetricCountResponse;
import io.github.ekrosrb.doc.repository.model.DocumentKind;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface MetricsApi {

  List<MetricCountResponse> countByType(@RequestParam(required = false) DocumentKind kind);
}
