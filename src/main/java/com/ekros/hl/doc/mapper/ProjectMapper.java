package com.ekros.hl.doc.mapper;

import com.ekros.hl.doc.controller.model.project.ProjectsInfoResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProjectMapper {

  ProjectsInfoResponse mapToProjectInfoResponse(String projectKey, long documents);
}
