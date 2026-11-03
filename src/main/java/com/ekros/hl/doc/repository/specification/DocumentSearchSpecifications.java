package com.ekros.hl.doc.repository.specification;

import com.ekros.hl.doc.controller.model.document.DocumentSearchRequest;
import com.ekros.hl.doc.repository.model.DocumentData;
import com.ekros.hl.doc.repository.model.DocumentRelationData;
import com.ekros.hl.doc.repository.model.DocumentRelationType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class DocumentSearchSpecifications {

  private static final String SEARCH_VECTOR_MATCH = "? @@ websearch_to_tsquery('english'::regconfig, ?)";
  private static final String SEARCH_VECTOR_RANK = "ts_rank(?, websearch_to_tsquery('english'::regconfig, ?))";
  private static final Pattern QUERY_SYNTAX = Pattern.compile("\"|(^|\\s)-|(^|\\s)or(\\s|$)", Pattern.CASE_INSENSITIVE);
  private static final Pattern NON_WORD = Pattern.compile("[^\\p{L}\\p{N}]+");
  private static final String OR_OPERATOR = "or";
  private static final String ANY_WORD_SEPARATOR = " " + OR_OPERATOR + " ";

  public static Specification<DocumentData> withFilters(DocumentSearchRequest searchRequest) {
    return (root, query, criteriaBuilder) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      if (searchRequest.getKind() != null) {
        predicates.add(criteriaBuilder.equal(root.get("kind"), searchRequest.getKind()));
      }

      if (searchRequest.getScope() != null) {
        predicates.add(criteriaBuilder.equal(root.get("scope"), searchRequest.getScope()));
      }

      if (searchRequest.getType() != null) {
        predicates.add(criteriaBuilder.equal(root.get("type"), searchRequest.getType()));
      }

      if (searchRequest.getAuthor() != null) {
        predicates.add(criteriaBuilder.equal(root.get("author"), searchRequest.getAuthor()));
      }

      if (searchRequest.getMemoryType() != null) {
        predicates.add(criteriaBuilder.equal(root.get("memoryType"), searchRequest.getMemoryType()));
      }

      if (!searchRequest.isIncludeSuperseded()) {
        var subquery = query.subquery(Integer.class);
        var relation = subquery.from(DocumentRelationData.class);
        subquery.select(criteriaBuilder.literal(1))
            .where(
                criteriaBuilder.equal(relation.get("id").get("relatedDocumentId"), root.get("id")),
                criteriaBuilder.equal(relation.get("type"), DocumentRelationType.SUPERSEDES),
                criteriaBuilder.isNull(relation.get("document").get("deletedAt")));
        predicates.add(criteriaBuilder.not(criteriaBuilder.exists(subquery)));
      }

      if (StringUtils.isNotBlank(searchRequest.getAuthorName())) {
        predicates.add(criteriaBuilder.equal(root.get("authorName"), searchRequest.getAuthorName()));
      }

      if (StringUtils.isNotBlank(searchRequest.getProjectKey())) {
        predicates.add(criteriaBuilder.equal(root.get("projectKey"), searchRequest.getProjectKey()));
      }

      if (StringUtils.isNotBlank(searchRequest.getQuery())) {
        predicates.add(criteriaBuilder.isTrue(
            ((HibernateCriteriaBuilder) criteriaBuilder).sql(SEARCH_VECTOR_MATCH, Boolean.class,
                root.get("searchVector"),
                criteriaBuilder.literal(searchRequest.getQuery())
            )
        ));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Rewrites a query of plain words so that any of the words matches.
   * Returns empty when the query has fewer than two words or uses phrase, OR or exclusion syntax.
   */
  public static Optional<String> buildAnyWordQuery(String query) {
    if (StringUtils.isBlank(query) || QUERY_SYNTAX.matcher(query).find()) {
      return Optional.empty();
    }

    var words = extractWords(query);
    return words.size() < 2 ? Optional.empty() : Optional.of(String.join(ANY_WORD_SEPARATOR, words));
  }

  /**
   * Rewrites free text, such as a task description, into a query that matches any of its words.
   * Search syntax in the text is treated as plain words. Returns empty when the text has no words.
   */
  public static Optional<String> buildFreeTextQuery(String text) {
    if (StringUtils.isBlank(text)) {
      return Optional.empty();
    }

    var words = extractWords(text).stream()
        .filter(word -> !OR_OPERATOR.equalsIgnoreCase(word))
        .toList();
    return words.isEmpty() ? Optional.empty() : Optional.of(String.join(ANY_WORD_SEPARATOR, words));
  }

  /**
   * Leaves out entries that belong to a project other than the given one. Keeps everything when no project is given.
   */
  public static Specification<DocumentData> withoutOtherProjects(String projectKey) {
    return (root, query, criteriaBuilder) -> StringUtils.isBlank(projectKey)
        ? criteriaBuilder.conjunction()
        : criteriaBuilder.or(
            criteriaBuilder.isNull(root.get("projectKey")),
            criteriaBuilder.equal(root.get("projectKey"), projectKey));
  }

  public static Specification<DocumentData> withIds(Collection<Long> ids) {
    return (root, query, criteriaBuilder) -> root.get("id").in(ids);
  }

  private static List<String> extractWords(String text) {
    return Arrays.stream(NON_WORD.split(text))
        .filter(StringUtils::isNotBlank)
        .distinct()
        .toList();
  }

  /**
   * Orders search results by relevance to the query when one is given, then by the newest update.
   */
  public static Specification<DocumentData> withOrder(DocumentSearchRequest searchRequest) {
    return (root, query, criteriaBuilder) -> {
      if (Long.class.equals(query.getResultType())) {
        return criteriaBuilder.conjunction();
      }

      var orders = new ArrayList<Order>();
      if (StringUtils.isNotBlank(searchRequest.getQuery())) {
        orders.add(criteriaBuilder.desc(((HibernateCriteriaBuilder) criteriaBuilder).sql(SEARCH_VECTOR_RANK, Double.class,
            root.get("searchVector"),
            criteriaBuilder.literal(searchRequest.getQuery()))));
      }
      orders.add(criteriaBuilder.desc(root.get("updatedAt")));
      orders.add(criteriaBuilder.desc(root.get("id")));

      query.orderBy(orders);
      return criteriaBuilder.conjunction();
    };
  }
}