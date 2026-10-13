package com.ekros.hl.doc.mcp.tools;

import com.ekros.hl.doc.controller.model.document.DocumentRelationCreateRequest;
import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.mcp.ToonConverter;
import com.ekros.hl.doc.mcp.model.DocumentCreateMcpRequest;
import com.ekros.hl.doc.mcp.model.DocumentSearchMcpRequest;
import com.ekros.hl.doc.mcp.model.MemoryCreateMcpRequest;
import com.ekros.hl.doc.mcp.model.MemorySearchMcpRequest;
import com.ekros.hl.doc.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
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

  @McpTool(name = "createDocument", description = "Created new document")
  public String createDocument(@Valid DocumentCreateMcpRequest request, McpSyncRequestContext context) {
    var agentName = getAgentName(context);
    log.info("Received mcp request to create new document from: [{}]", agentName);
    return toonConverter.toToon(documentService.createDocument(documentMapper.mapToDocumentCreateRequest(request, agentName)));
  }

  @McpTool(name = "createMemory", description = "Create new memory document")
  public String createMemory(@Valid MemoryCreateMcpRequest request, McpSyncRequestContext context) {
    var agentName = getAgentName(context);
    log.info("Received mcp request to create new memory document from: [{}]", agentName);
    return toonConverter.toToon(documentService.createDocument(documentMapper.mapToMemoryCreateRequest(request, agentName)));
  }

  @McpTool(name = "getDocument", description = "Returns full document/memory by id")
  public String getDocument(@Positive Long id, boolean includeContent, McpSyncRequestContext context) {
    log.info("Received mcp request to get document by id from: [{}]", getAgentName(context));
    return toonConverter.toToon(documentService.getDocument(id, includeContent));
  }

  @McpTool(name = "createDocumentRelation", description = "Add relation between two documents")
  public void createDocumentRelation(@Valid DocumentRelationCreateRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to create document relation by id from: [{}]", getAgentName(context));
    documentService.createDocumentRelation(request);
  }

  @McpTool(name = "searchDocuments", description = "Search documents using property fields and/or query")
  public String searchDocuments(@Valid DocumentSearchMcpRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to search documents from [{}]", getAgentName(context));
    return toonConverter.toToon(documentService.search(documentMapper.mapToDocumentSearchRequest(request)));
  }

  @McpTool(name = "searchMemories", description = "Search memories using property fields, memory type and/or query")
  public String searchMemories(@Valid MemorySearchMcpRequest request, McpSyncRequestContext context) {
    log.info("Received mcp request to search memories from [{}]", getAgentName(context));
    return toonConverter.toToon(documentService.search(documentMapper.mapToMemorySearchRequest(request)));
  }

}
