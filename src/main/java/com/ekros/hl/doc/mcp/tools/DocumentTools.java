package com.ekros.hl.doc.mcp.tools;

import com.ekros.hl.doc.mapper.DocumentMapper;
import com.ekros.hl.doc.mcp.ToonConverter;
import com.ekros.hl.doc.mcp.model.DocumentCreateMcpRequest;
import com.ekros.hl.doc.service.DocumentService;
import com.ekros.hl.doc.validation.ValidUUID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import static com.ekros.hl.doc.repository.model.DocumentKind.DOCUMENT;
import static com.ekros.hl.doc.repository.model.DocumentKind.MEMORY;
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
    log.info("Received mcp request to create new document from: {}", agentName);
    return toonConverter.toToon(documentService.createDocument(documentMapper.mapToDocumentCreateRequest(request, DOCUMENT, agentName)));
  }

  @McpTool(name = "createMemory", description = "Create new memory document")
  public String createMemory(@Valid DocumentCreateMcpRequest request, McpSyncRequestContext context) {
    var agentName = getAgentName(context);
    log.info("Received mcp request to create new memory document from: {}", agentName);
    return toonConverter.toToon(documentService.createDocument(documentMapper.mapToDocumentCreateRequest(request, MEMORY, agentName)));
  }

  @McpTool(name = "getDocument", description = "Returns full document/memory by id")
  public String getDocument(@ValidUUID String id, boolean includeContent, McpSyncRequestContext context) {
    log.info("Received mcp request to get document by id from: {}", getAgentName(context));
    return toonConverter.toToon(documentService.getDocument(id, includeContent));
  }

}
