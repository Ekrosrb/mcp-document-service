package io.github.ekrosrb.doc.repository;

import io.github.ekrosrb.doc.repository.model.DocumentRelationData;
import io.github.ekrosrb.doc.repository.model.DocumentRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentRelationRepository extends JpaRepository<DocumentRelationData, DocumentRelationId> {
  /**
   * Returns the relations that point to the given document from documents that are not deleted.
   */
  List<DocumentRelationData> findAllByIdRelatedDocumentIdAndDocumentDeletedAtIsNull(Long relatedDocumentId);

  /**
   * Returns the relations that start at or point to any of the given documents.
   */
  List<DocumentRelationData> findAllByIdDocumentIdInOrIdRelatedDocumentIdIn(Collection<Long> documentIds,
                                                                            Collection<Long> relatedDocumentIds);
}
