package io.github.ekrosrb.doc.mapper;

import io.github.ekrosrb.doc.controller.model.project.ProjectsInfoResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProjectMapper {

  ProjectsInfoResponse mapToProjectInfoResponse(String projectKey, long documents);
}
