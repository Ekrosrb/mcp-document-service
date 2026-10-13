package com.ekros.hl.doc.util;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.experimental.UtilityClass;
import org.springframework.ai.mcp.annotation.context.McpRequestContextTypes;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;

import java.util.Optional;

@UtilityClass
public class McpUtil {
  public static String getAgentName(McpSyncRequestContext context) {
    return Optional.ofNullable(context)
        .map(McpRequestContextTypes::clientInfo)
        .map(McpSchema.Implementation::name).orElse("NO_NAME_PRESENT");
  }
}
