package com.ekros.hl.doc.mapper;

import com.ekros.hl.doc.controller.model.document.*;
import com.ekros.hl.doc.mcp.model.DocumentCreateMcpRequest;
import com.ekros.hl.doc.mcp.model.DocumentSearchMcpRequest;
import com.ekros.hl.doc.mcp.model.MemoryCreateMcpRequest;
import com.ekros.hl.doc.mcp.model.MemorySearchMcpRequest;
import com.ekros.hl.doc.repository.model.*;
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
  @Mapping(target = "searchVector", ignore = true)
  @Mapping(target = "memoryType", source = "request", qualifiedByName = "mapMemoryType")
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
  @Mapping(target = "relations", source = "documentData.relations", conditionExpression = "java(includeRelations)")
  DocumentResponse mapToDocumentResponseLight(DocumentData documentData, boolean includeContent, boolean includeSummary, boolean includeRelations);

  @Mapping(target = "author", constant = "AGENT")
  @Mapping(target = "kind", constant = "DOCUMENT")
  @Mapping(target = "memoryType", ignore = true)
  DocumentCreateRequest mapToDocumentCreateRequest(DocumentCreateMcpRequest request, String authorName);

  @Mapping(target = "author", constant = "AGENT")
  @Mapping(target = "kind", constant = "MEMORY")
  DocumentCreateRequest mapToMemoryCreateRequest(MemoryCreateMcpRequest request, String authorName);

  @Mapping(target = "kind", constant = "DOCUMENT")
  @Mapping(target = "memoryType", ignore = true)
  DocumentSearchRequest mapToDocumentSearchRequest(DocumentSearchMcpRequest request);

  @Mapping(target = "kind", constant = "MEMORY")
  DocumentSearchRequest mapToMemorySearchRequest(MemorySearchMcpRequest request);

  @Mapping(target = "id", source = "id.relatedDocumentId")
  @Mapping(target = "name", source = "documentRelationData.relatedDocument.name")
  DocumentRelation mapToDocumentRelation(DocumentRelationData documentRelationData);

  @Named("mapToReferencingDocumentRelation")
  @Mapping(target = "id", source = "id.documentId")
  @Mapping(target = "name", source = "documentRelationData.document.name")
  DocumentRelation mapToReferencingDocumentRelation(DocumentRelationData documentRelationData);

  DocumentRelationId mapToDocumentRelationId(DocumentRelationCreateRequest request);

  @Mapping(target = "id", source = "request")
  @Mapping(target = "document", ignore = true)
  @Mapping(target = "relatedDocument", ignore = true)
  DocumentRelationData mapToDocumentRelationData(DocumentRelationCreateRequest request);

  @Named("mapMemoryType")
  default MemoryType mapMemoryType(DocumentCreateRequest request) {
    if (request.getKind() != DocumentKind.MEMORY) {
      return null;
    }
    return Optional.ofNullable(request.getMemoryType()).orElse(MemoryType.OBSERVATION);
  }

  @Named("mapDocumentContent")
  default String mapDocumentContent(DocumentContentData content) {
    return Optional.ofNullable(content)
        .map(DocumentContentData::getContent)
        .orElse(null);
  }
}
