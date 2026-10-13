package com.ekros.hl.doc.repository;

import com.ekros.hl.doc.repository.model.DocumentRelationData;
import com.ekros.hl.doc.repository.model.DocumentRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRelationRepository extends JpaRepository<DocumentRelationData, DocumentRelationId> {

  /**
   * Returns the relations that start at the given document.
   */
  List<DocumentRelationData> findAllByIdDocumentId(Long documentId);

  /**
   * Returns the relations that point to the given document.
   */
  List<DocumentRelationData> findAllByIdRelatedDocumentId(Long relatedDocumentId);
}
