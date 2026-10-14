package com.ekros.hl.doc.mcp.tools;

import com.ekros.hl.doc.controller.model.document.DocumentRelationCreateRequest;
import com.ekros.hl.doc.controller.model.document.DocumentRelationRemoveRequest;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.mcp.McpDescriptions;
import com.ekros.hl.doc.mcp.ToonConverter;
import com.ekros.hl.doc.mcp.model.*;
import com.ekros.hl.doc.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

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
                            @McpToolParam(description = McpDescriptions.GET_INCLUDE_CONTENT, required = false) boolean includeContent, McpSyncRequestContext context) {
    log.info("Received mcp request to get document by id from: [{}]", getAgentName(context));
    return toonConverter.toToon(documentMapper.mapToDocumentMcpResponse(documentService.getDocument(id, includeContent)));
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
  public String searchDocuments(@McpToolParam(description = McpDescriptions.SEARCH_REQUEST) @Valid DocumentSearchMcpRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to search documents from [{}]", getAgentName(context));
    return toonConverter.toToon(documentMapper.mapToDocumentMcpResponses(
        documentService.search(documentMapper.mapToDocumentSearchRequest(request))));
  }

  @McpTool(name = "searchMemories", description = McpDescriptions.SEARCH_MEMORIES)
  public String searchMemories(@McpToolParam(description = McpDescriptions.SEARCH_REQUEST) @Valid MemorySearchMcpRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to search memories from [{}]", getAgentName(context));
    return toonConverter.toToon(documentMapper.mapToDocumentMcpResponses(
        documentService.search(documentMapper.mapToMemorySearchRequest(request))));
  }

}
