package io.github.ekrosrb.doc.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Directed, typed relation from one document to another.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"document", "relatedDocument"})
@Entity
@Table(name = "document_relations")
public class DocumentRelationData {

  @Builder.Default
  @EmbeddedId
  private DocumentRelationId id = new DocumentRelationId();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("documentId")
  @JoinColumn(name = "document_id")
  private DocumentData document;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("relatedDocumentId")
  @JoinColumn(name = "related_document_id")
  private DocumentData relatedDocument;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private DocumentRelationType type;
}
