package com.ekros.hl.doc.mcp.tools;

import com.ekros.hl.doc.controller.model.document.DocumentRecallRequest;
import com.ekros.hl.doc.controller.model.document.DocumentRelationCreateRequest;
import com.ekros.hl.doc.controller.model.document.DocumentRelationRemoveRequest;
import com.ekros.hl.doc.controller.model.document.DocumentSearchRequest;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.mcp.McpDescriptions;
import com.ekros.hl.doc.mcp.ToonConverter;
import com.ekros.hl.doc.mcp.model.*;
import com.ekros.hl.doc.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

import static com.ekros.hl.doc.util.McpUtil.getAgentName;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class DocumentTools {

  private final DocumentService documentService;
  private final DocumentMapper documentMapper;
  private final ToonConverter toonConverter;

  @McpTool(name = "createDocument", description = McpDescriptions.CREATE_DOCUMENT)
  public String createDocument(@McpToolParam(description = McpDescriptions.CREATE_DOCUMENT_REQUEST) @Valid DocumentCreateMcpRequest request, McpSyncRequestContext context) {
    var agentName = getAgentName(context);
    log.info("Received mcp request to create new document from: [{}]", agentName);
    return toonConverter.toToon(documentService.createDocument(documentMapper.mapToDocumentCreateRequest(request, agentName)));
  }

  @McpTool(name = "updateDocument", description = McpDescriptions.UPDATE_DOCUMENT)
  public void updateDocument(@McpToolParam(description = McpDescriptions.ID) @Positive Long id, @McpToolParam(description = McpDescriptions.UPDATE_REQUEST) @Valid DocumentUpdateMcpRequest request, McpSyncRequestContext context) {
    var agentName = getAgentName(context);
    log.info("Received mcp request to update existing document [{}] from: [{}]", id, agentName);
    documentService.updateDocument(id, documentMapper.mapToDocumentUpdateRequest(request), true);
  }

  @McpTool(name = "removeDocument", description = McpDescriptions.REMOVE_DOCUMENT)
  public void removeDocument(@McpToolParam(description = McpDescriptions.ID) @Positive Long id, McpSyncRequestContext context) {
    log.info("Received mcp request to remove document [{}] from: [{}]", id, getAgentName(context));
    documentService.removeDocument(id, true);
  }

  @McpTool(name = "createMemory", description = McpDescriptions.CREATE_MEMORY)
  public String createMemory(@McpToolParam(description = McpDescriptions.CREATE_MEMORY_REQUEST) @Valid MemoryCreateMcpRequest request, McpSyncRequestContext context) {
    var agentName = getAgentName(context);
    log.info("Received mcp request to create new memory document from: [{}]", agentName);
    return toonConverter.toToon(documentService.createDocument(documentMapper.mapToMemoryCreateRequest(request, agentName)));
  }

  @McpTool(name = "getDocument", description = McpDescriptions.GET_DOCUMENT)
  public String getDocument(@McpToolParam(description = McpDescriptions.ID) @Positive Long id,
                            @McpToolParam(description = McpDescriptions.GET_INCLUDE_CONTENT, required = false) Boolean includeContent, McpSyncRequestContext context) {
    log.info("Received mcp request to get document by id from: [{}]", getAgentName(context));
    return toonConverter.toToon(documentMapper.mapToDocumentMcpResponse(
        documentService.getDocument(id, Boolean.TRUE.equals(includeContent))));
  }

  @McpTool(name = "createDocumentRelation", description = McpDescriptions.CREATE_DOCUMENT_RELATION)
  public void createDocumentRelation(@McpToolParam(description = McpDescriptions.RELATION_REQUEST) @Valid DocumentRelationCreateRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to create document relation from: [{}]", getAgentName(context));
    documentService.createDocumentRelation(request, true);
  }

  @McpTool(name = "removeDocumentRelation", description = McpDescriptions.REMOVE_DOCUMENT_RELATION)
  public void removeDocumentRelation(@McpToolParam(description = McpDescriptions.RELATION_REMOVE_REQUEST) @Valid DocumentRelationRemoveRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to remove document relation from: [{}]", getAgentName(context));
    documentService.removeDocumentRelation(request, true);
  }

  @McpTool(name = "searchDocuments", description = McpDescriptions.SEARCH_DOCUMENTS)
  public String searchDocuments(@McpToolParam(description = McpDescriptions.SEARCH_REQUEST) @Valid DocumentSearchMcpRequest request,
                                @McpToolParam(description = McpDescriptions.PAGE, required = false) @PositiveOrZero Integer page,
                                @McpToolParam(description = McpDescriptions.SEARCH_PAGE_SIZE, required = false) @Positive @Max(DocumentSearchRequest.MAX_MCP_PAGE_SIZE) Integer size,
                                McpSyncRequestContext context) {
    log.info("Received mcp request to search documents from [{}]", getAgentName(context));
    return toonConverter.toToon(documentMapper.mapToDocumentMcpResponses(
        documentService.search(documentMapper.mapToDocumentSearchRequest(request), buildPageable(page, size)).getContent()));
  }

  @McpTool(name = "searchMemories", description = McpDescriptions.SEARCH_MEMORIES)
  public String searchMemories(@McpToolParam(description = McpDescriptions.SEARCH_REQUEST) @Valid MemorySearchMcpRequest request,
                               @McpToolParam(description = McpDescriptions.PAGE, required = false) @PositiveOrZero Integer page,
                               @McpToolParam(description = McpDescriptions.SEARCH_PAGE_SIZE, required = false) @Positive @Max(DocumentSearchRequest.MAX_MCP_PAGE_SIZE) Integer size,
                               McpSyncRequestContext context) {
    log.info("Received mcp request to search memories from [{}]", getAgentName(context));
    return toonConverter.toToon(documentMapper.mapToDocumentMcpResponses(
        documentService.search(documentMapper.mapToMemorySearchRequest(request), buildPageable(page, size)).getContent()));
  }

  @McpTool(name = "recall", description = McpDescriptions.RECALL)
  public String recall(@McpToolParam(description = McpDescriptions.RECALL_TASK) @NotBlank String task,
                       @McpToolParam(description = McpDescriptions.RECALL_PROJECT_KEY, required = false) String projectKey,
                       @McpToolParam(description = McpDescriptions.RECALL_LIMIT, required = false) @Positive @Max(DocumentRecallRequest.MAX_LIMIT) Integer limit,
                       @McpToolParam(description = McpDescriptions.RECALL_DEPTH, required = false) @PositiveOrZero @Max(DocumentRecallRequest.MAX_DEPTH) Integer depth,
                       @McpToolParam(description = McpDescriptions.RECALL_INCLUDE_CONTENT, required = false) Boolean includeContent,
                       McpSyncRequestContext context) {
    log.info("Received mcp request to recall context from [{}]", getAgentName(context));
    var request = DocumentRecallRequest.builder()
        .task(task)
        .projectKey(projectKey)
        .limit(Optional.ofNullable(limit).orElse(DocumentRecallRequest.DEFAULT_LIMIT))
        .depth(Optional.ofNullable(depth).orElse(DocumentRecallRequest.DEFAULT_DEPTH))
        .includeContent(Boolean.TRUE.equals(includeContent))
        .build();
    return toonConverter.toToon(documentMapper.mapToRecallMcpResponse(documentService.recall(request)));
  }

  private Pageable buildPageable(Integer page, Integer size) {
    return PageRequest.of(Optional.ofNullable(page).orElse(0),
        Optional.ofNullable(size).orElse(DocumentSearchRequest.DEFAULT_PAGE_SIZE));
  }

}
