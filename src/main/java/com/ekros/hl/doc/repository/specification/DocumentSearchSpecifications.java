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

public class DocumentSearchSpecifications {

  private static final String SEARCH_VECTOR_MATCH = "? @@ websearch_to_tsquery('english'::regconfig, ?)";
  private static final String SEARCH_VECTOR_RANK = "ts_rank(?, websearch_to_tsquery('english'::regconfig, ?))";

  public static Specification<DocumentData> withFilters(DocumentSearchRequest searchRequest) {
    return (root, query, criteriaBuilder) -> {
      var predicates = new ArrayList<Predicate>();

      if (searchRequest.getKind() != null) {
        predicates.add(criteriaBuilder.equal(root.get("kind"), searchRequest.getKind()));
      }

      if (searchRequest.getScope() != null) {
        predicates.add(criteriaBuilder.equal(root.get("scope"), searchRequest.getScope()));
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
                criteriaBuilder.equal(relation.get("type"), DocumentRelationType.SUPERSEDES));
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