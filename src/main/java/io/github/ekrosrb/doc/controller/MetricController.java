package io.github.ekrosrb.doc.controller;

import io.github.ekrosrb.doc.controller.api.MetricsApi;
import io.github.ekrosrb.doc.controller.model.metric.MetricCountResponse;
import io.github.ekrosrb.doc.repository.model.DocumentKind;
import io.github.ekrosrb.doc.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/metric")
public class MetricController implements MetricsApi {

  private final MetricService metricService;

  @Override
  @GetMapping("/count/type")
  public List<MetricCountResponse> countByType(@RequestParam(required = false) DocumentKind kind) {
    return metricService.countByType(kind);
  }
}
