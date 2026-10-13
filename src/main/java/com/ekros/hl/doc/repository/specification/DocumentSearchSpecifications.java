package com.ekros.hl.doc.repository.specification;

import com.ekros.hl.doc.controller.model.document.DocumentSearchRequest;
import com.ekros.hl.doc.repository.model.DocumentData;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class DocumentSearchSpecifications {

  private static final String SEARCH_VECTOR_MATCH = "? @@ websearch_to_tsquery('english'::regconfig, ?)";

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
}