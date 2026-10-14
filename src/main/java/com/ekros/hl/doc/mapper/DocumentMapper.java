package com.ekros.hl.doc.mapper;

import com.ekros.hl.doc.controller.model.document.*;
import com.ekros.hl.doc.mcp.model.*;
import com.ekros.hl.doc.repository.model.*;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
  @Mapping(target = "allowMcpWrite", source = "request.allowMcpWrite", defaultValue = "false")
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
  @Mapping(target = "allowMcpWrite", constant = "true")
  DocumentCreateRequest mapToDocumentCreateRequest(DocumentCreateMcpRequest request, String authorName);

  @Mapping(target = "author", constant = "AGENT")
  @Mapping(target = "kind", constant = "MEMORY")
  @Mapping(target = "allowMcpWrite", constant = "true")
  DocumentCreateRequest mapToMemoryCreateRequest(MemoryCreateMcpRequest request, String authorName);

  @Mapping(target = "allowMcpWrite", ignore = true)
  @Mapping(target = "authorName", ignore = true)
  DocumentUpdateRequest mapToDocumentUpdateRequest(DocumentUpdateMcpRequest request);

  @Mapping(target = "kind", constant = "DOCUMENT")
  @Mapping(target = "memoryType", ignore = true)
  DocumentSearchRequest mapToDocumentSearchRequest(DocumentSearchMcpRequest request);

  @Mapping(target = "kind", constant = "MEMORY")
  DocumentSearchRequest mapToMemorySearchRequest(MemorySearchMcpRequest request);

  @Mapping(target = "id", source = "id.relatedDocumentId")
  @Mapping(target = "name", source = "documentRelationData.relatedDocument.name")
  DocumentRelation mapToDocumentRelation(DocumentRelationData documentRelationData);

  /**
   * Maps the outgoing relations of a document, leaving out those that point to deleted documents.
   */
  default List<DocumentRelation> mapToLiveDocumentRelations(Set<DocumentRelationData> relations) {
    if (relations == null) {
      return null;
    }
    return relations.stream()
        .filter(relation -> relation.getRelatedDocument().getDeletedAt() == null)
        .map(this::mapToDocumentRelation)
        .toList();
  }

  @Named("mapToReferencingDocumentRelation")
  @Mapping(target = "id", source = "id.documentId")
  @Mapping(target = "name", source = "documentRelationData.document.name")
  DocumentRelation mapToReferencingDocumentRelation(DocumentRelationData documentRelationData);

  DocumentRelationId mapToDocumentRelationId(DocumentRelationCreateRequest request);

  DocumentRelationId mapToDocumentRelationId(DocumentRelationRemoveRequest request);

  @Mapping(target = "id", expression = "java(new DocumentRelationId(document.getId(), relation.getRelatedDocumentId()))")
  @Mapping(target = "document", source = "document")
  @Mapping(target = "relatedDocument", source = "relatedDocument")
  @Mapping(target = "type", source = "relation.type")
  DocumentRelationData mapToDocumentRelationData(DocumentData document, DocumentData relatedDocument,
                                                 DocumentCreateRequest.DocumentRelation relation);

  @Mapping(target = "id", source = "request")
  @Mapping(target = "document", ignore = true)
  @Mapping(target = "relatedDocument", ignore = true)
  DocumentRelationData mapToDocumentRelationData(DocumentRelationCreateRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "content", ignore = true)
  void updateDocument(@MappingTarget DocumentData documentData, DocumentUpdateRequest request);

  @AfterMapping
  default void updateDocumentContent(@MappingTarget DocumentData documentData, DocumentUpdateRequest request) {
    if (request.getContent() != null) {
      documentData.getContent().setContent(request.getContent());
    }
  }

  @Mapping(target = "allowWrite", source = "response.allowMcpWrite")
  DocumentMcpResponse mapToDocumentMcpResponse(DocumentResponse response);

  List<DocumentMcpResponse> mapToDocumentMcpResponses(List<DocumentResponse> responses);

  RecallMcpResponse mapToRecallMcpResponse(DocumentRecallResponse response);

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
