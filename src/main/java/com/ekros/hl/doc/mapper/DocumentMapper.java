package com.ekros.hl.doc.mapper;

import com.ekros.hl.doc.controller.model.DocumentCreateRequest;
import com.ekros.hl.doc.controller.model.DocumentCreateResponse;
import com.ekros.hl.doc.controller.model.DocumentResponse;
import com.ekros.hl.doc.mcp.model.DocumentCreateMcpRequest;
import com.ekros.hl.doc.repository.model.DocumentContentData;
import com.ekros.hl.doc.repository.model.DocumentData;
import com.ekros.hl.doc.repository.model.DocumentKind;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DocumentMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "type", source = "request.type", defaultValue = "TEXT")
  @Mapping(target = "relations", ignore = true)
  @Mapping(target = "content", source = "content")
  DocumentData mapToDocumentData(DocumentCreateRequest request, DocumentContentData content);

  @Mapping(target = "id", ignore = true)
  DocumentContentData mapToDocumentContentData(DocumentCreateRequest request);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "id", source = "documentData.id")
  DocumentCreateResponse mapToDocumentCreateResponse(DocumentData documentData);

  @Mapping(target = "content", source = "documentData.content", conditionExpression = "java(includeContent)", qualifiedByName = "mapDocumentContent")
  DocumentResponse mapToDocumentResponse(DocumentData documentData, boolean includeContent);

  @Mapping(target = "content", source = "documentData.content", conditionExpression = "java(includeContent)", qualifiedByName = "mapDocumentContent")
  @Mapping(target = "summary", source = "documentData.summary", conditionExpression = "java(includeSummary)")
  DocumentResponse mapToDocumentResponseLight(DocumentData documentData, boolean includeContent, boolean includeSummary);

  @Mapping(target = "author", constant = "AGENT")
  DocumentCreateRequest mapToDocumentCreateRequest(DocumentCreateMcpRequest request, DocumentKind kind, String authorName);

  @Named("mapDocumentContent")
  default String mapDocumentContent(DocumentContentData content) {
    return Optional.ofNullable(content)
        .map(DocumentContentData::getContent)
        .orElse(null);
  }
}
