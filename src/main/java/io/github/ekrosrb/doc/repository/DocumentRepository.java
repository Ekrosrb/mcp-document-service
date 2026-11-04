package io.github.ekrosrb.doc.repository;

import io.github.ekrosrb.doc.repository.model.DocumentData;
import io.github.ekrosrb.doc.repository.model.DocumentKind;
import io.github.ekrosrb.doc.repository.model.DocumentRelationType;
import io.github.ekrosrb.doc.repository.projection.DocumentRankProjection;
import io.github.ekrosrb.doc.repository.projection.DocumentTypeCountProjection;
import io.github.ekrosrb.doc.repository.projection.ProjectDocumentsCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentData, Long>, JpaSpecificationExecutor<DocumentData> {

  @Query("SELECT d.projectKey AS projectKey, COUNT(d) AS count " +
      "FROM DocumentData d " +
      "WHERE d.projectKey IN :projectKeys AND d.deletedAt IS NULL " +
      "GROUP BY d.projectKey")
  List<ProjectDocumentsCountProjection> countBySpecificProjectKeys(@Param("projectKeys") Collection<String> projectKeys);

  /**
   * Counts live entries of the given kinds per type, leaving out entries superseded by a live entry.
   */
  @Query("SELECT d.type AS type, COUNT(d) AS count " +
      "FROM DocumentData d " +
      "WHERE d.kind IN :kinds AND d.deletedAt IS NULL " +
      "AND NOT EXISTS (SELECT 1 FROM DocumentRelationData r " +
      "WHERE r.id.relatedDocumentId = d.id AND r.type = :supersedes AND r.document.deletedAt IS NULL) " +
      "GROUP BY d.type " +
      "ORDER BY d.type")
  List<DocumentTypeCountProjection> countByType(@Param("kinds") Collection<DocumentKind> kinds,
                                                @Param("supersedes") DocumentRelationType supersedes);

  /**
   * Returns the full-text rank of the given documents for a websearch query, as used to order search results.
   */
  @Query(value = "SELECT d.id AS id, "
      + "ts_rank(d.search_vector, websearch_to_tsquery('english'::regconfig, :query))::float8 AS rank "
      + "FROM document d WHERE d.id IN (:ids)", nativeQuery = true)
  List<DocumentRankProjection> findRanks(@Param("ids") Collection<Long> ids, @Param("query") String query);

  Optional<DocumentData> findByIdAndDeletedAtIsNull(Long id);

  boolean existsByIdAndDeletedAtIsNull(Long id);

  Optional<DocumentData> findByIdAndDeletedAtIsNotNull(Long id);

  Page<DocumentData> findAllByDeletedAtIsNotNull(Pageable pageable);

  /**
   * Marks a live document as deleted at the given time. Returns the number of documents marked.
   */
  @Modifying
  @Query(value = "UPDATE document SET deleted_at = :deletedAt WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
  int markDeleted(@Param("id") Long id, @Param("deletedAt") Timestamp deletedAt);

  /**
   * Clears the deletion mark of a document. Returns the number of documents restored.
   */
  @Modifying
  @Query(value = "UPDATE document SET deleted_at = NULL WHERE id = :id AND deleted_at IS NOT NULL", nativeQuery = true)
  int clearDeleted(@Param("id") Long id);
}
