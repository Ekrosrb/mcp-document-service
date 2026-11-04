package io.github.ekrosrb.doc.mcp;

/**
 * Texts that describe the MCP tools and their parameters to the calling agent.
 */
public final class McpDescriptions {

  private static final String SEARCH_ORDER = "Most relevant first when a query is given, otherwise most recently "
      + "updated first.";

  public static final String CREATE_DOCUMENT = "Store a long-form document (guide, spec, reference). "
      + "For a short fact, decision or note use createMemory. Returns the new id.";
  public static final String CREATE_MEMORY = "Store a short note that will still matter after this conversation: "
      + "a decision, rule, fact, pitfall or status. Search first to avoid duplicates. Returns the new id.";
  public static final String GET_DOCUMENT = "Read one document or memory by id, with its relations (outgoing) "
      + "and referencedBy (incoming).";
  public static final String UPDATE_DOCUMENT = "Change fields of a document or memory. Omitted fields stay as they are. "
      + "Fails when allowWrite is false.";
  public static final String REMOVE_DOCUMENT = "Delete a document or memory. It disappears from search, recall "
      + "and getDocument, and links to it are hidden; only a person can restore it. Fails when allowWrite is false.";
  public static final String CREATE_DOCUMENT_RELATION = "Link two documents or memories. "
      + "Requires allowWrite on documentId only.";
  public static final String REMOVE_DOCUMENT_RELATION = "Unlink two documents or memories. "
      + "Requires allowWrite on documentId only.";
  public static final String SEARCH_DOCUMENTS = "Find documents by filters and/or a full-text query. "
      + "Returns id, name and metadata; use the include flags or getDocument for the text. "
      + SEARCH_ORDER;
  public static final String SEARCH_MEMORIES = "Find memories by filters, memory type and/or a full-text query. "
      + "Call it before starting a task. Returns id, name and metadata; "
      + "use the include flags or getDocument for the text. " + SEARCH_ORDER;
  public static final String RECALL = "Gather what is known for a task you are about to start. Call it first. "
      + "Returns matches: memories and documents containing any word of the task, most relevant first, "
      + "far weaker matches left out; and linked: entries linked to the matches in either direction, nearest first. "
      + "Each entry comes with its summary and relations; replaced entries are left out. "
      + "Use getDocument for the full text, searchMemories or searchDocuments for exact filters.";
  public static final String GET_PROJECTS_INFO = "List project keys with the number of documents in each. "
      + "Use it to find the projectKey to filter by.";

  public static final String ID = "Id of the document or memory, as returned by create and search.";
  public static final String GET_INCLUDE_CONTENT = "Return the full content. Default false.";
  public static final String CREATE_DOCUMENT_REQUEST = "The document to store.";
  public static final String CREATE_MEMORY_REQUEST = "The memory to store.";
  public static final String UPDATE_REQUEST = "The fields to change.";
  public static final String RELATION_REQUEST = "The relation to create.";
  public static final String RELATION_REMOVE_REQUEST = "The relation to remove.";
  public static final String SEARCH_REQUEST = "Filters and options. All are optional and combined with AND.";
  public static final String PAGE = "Page to fetch, starting at 0. Default 0.";
  public static final String PAGE_SIZE = "Projects per page. Default 100.";
  public static final String SEARCH_PAGE_SIZE = "Results per page, 1 to 50. Default 10. "
      + "Fewer results than this means there are no more pages.";

  public static final String SCOPE = "Where it applies. GLOBAL: every project. "
      + "PROJECT: one project, projectKey is required. AGENT: notes of one agent, not tied to a project.";
  public static final String TYPE = "Format of the content. Default TEXT. CUSTOM requires customExtension.";
  public static final String NAME = "Short title, shown in search results and relations.";
  public static final String PROJECT_KEY = "Key of the project it belongs to, for example the repository name. "
      + "Required for scope PROJECT. An unknown key creates the project.";
  public static final String SUMMARY = "One or two sentences that say what this is, "
      + "understandable without the content. Search returns it instead of the content.";
  public static final String CONTENT = "The full text.";
  public static final String CUSTOM_EXTENSION = "File extension for type CUSTOM, for example .adoc.";

  private static final String MEMORY_TYPE_VALUES = "DECISION: a choice made and why, follow it. "
      + "RULE: how the user or project wants things done, always apply it. "
      + "FACT: verified, rely on it. "
      + "OBSERVATION: noticed but not verified, check before relying on it. "
      + "PROCEDURE: steps that worked, reuse them. "
      + "PITFALL: something that failed or breaks, avoid it. "
      + "STATUS: where work stands, may be out of date.";
  public static final String MEMORY_TYPE = "How a reader should treat the memory. Default OBSERVATION. "
      + MEMORY_TYPE_VALUES;
  public static final String UPDATE_MEMORY_TYPE = "New type of a memory. Only for memories, rejected for documents. "
      + MEMORY_TYPE_VALUES;

  public static final String SEARCH_SCOPE = "Return only this scope: GLOBAL, PROJECT or AGENT.";
  public static final String SEARCH_AUTHOR = "Return only entries written by a HUMAN or by an AGENT.";
  public static final String SEARCH_AUTHOR_NAME = "Return only entries by this author. "
      + "For an agent it is its MCP client name.";
  public static final String SEARCH_PROJECT_KEY = "Return only entries of this project.";
  public static final String SEARCH_QUERY = "English full-text search over name, summary and content. "
      + "Returns entries with all words; when there are none, entries with any of the words. "
      + "\"quoted phrase\", OR and -word to exclude are supported and turn that fallback off. "
      + "It is not semantic, so use words likely to appear in the text.";
  public static final String SEARCH_MEMORY_TYPE = "Return only memories of this type. " + MEMORY_TYPE_VALUES;
  public static final String INCLUDE_SUMMARY = "Add the summary to each result. Default false.";
  public static final String INCLUDE_CONTENT = "Add the full content to each result. Default false; "
      + "costly when there are many results.";
  public static final String INCLUDE_RELATIONS = "Add relations and referencedBy to each result. Default false.";
  public static final String INCLUDE_SUPERSEDED = "Also return entries replaced by a newer one "
      + "(targets of a SUPERSEDES relation). Default false. If nothing current matches, retry with true and "
      + "includeRelations; referencedBy then shows the newer entry.";

  public static final String RECALL_TASK = "What you are about to do, in a sentence or a few keywords. "
      + "Its words are matched against name, summary and content; it is not semantic, "
      + "so use words likely to appear in the text.";
  public static final String RECALL_PROJECT_KEY = "Key of the project you work in. "
      + "Entries of other projects are left out; global and agent entries stay. Default: all projects.";
  public static final String RECALL_LIMIT = "Maximum number of matches, and separately of linked entries, 1 to 50. "
      + "Default 10.";
  public static final String RECALL_DEPTH = "How many links to follow from the matches, 0 to 2. Default 1; "
      + "0 returns no linked entries.";
  public static final String RECALL_INCLUDE_CONTENT = "Add the full content to each entry. Default false; "
      + "costly when there are many entries.";

  public static final String RELATION_DOCUMENT_ID = "Id of the source: the document the relation starts from.";
  public static final String RELATION_RELATED_DOCUMENT_ID = "Id of the target document.";
  private static final String RELATION_TYPE_VALUES = "DEPENDS_ON: the source cannot be understood without the target, "
      + "read the target first. "
      + "SUPERSEDES: the source replaces the target, the target is outdated. "
      + "EXTENDS: the source adds detail to the target. "
      + "PART_OF: the source is a section of the target. "
      + "DERIVED_FROM: the source was produced from the target. "
      + "RELATED_TO: same topic, no stronger link.";
  public static final String RELATION_TYPE = "Meaning, read as: documentId <type> relatedDocumentId. "
      + RELATION_TYPE_VALUES;
  public static final String CREATE_RELATIONS = "Links from the new entry to existing documents or memories. "
      + "Optional. Fails when a related document does not exist.";
  public static final String CREATE_RELATION_TYPE = "Meaning, read as: new entry <type> relatedDocumentId; "
      + "the new entry is the source. " + RELATION_TYPE_VALUES;

  private McpDescriptions() {
  }
}
