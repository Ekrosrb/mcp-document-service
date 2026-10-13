package com.ekros.hl.doc.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document")
public class DocumentData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "kind")
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private DocumentKind kind;

  @Column(name = "scope")
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private DocumentScope scope;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private DocumentType type;

  @Column(name = "author")
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private AuthorType author;

  @Column(name = "name")
  private String name;

  @Column(name = "author_name")
  private String authorName;

  @Column(name = "project_key")
  private String projectKey;

  @Column(name = "summary")
  private String summary;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false)
  private DocumentContentData content;

  @Column(name = "custom_extension")
  private String customExtension;

  @CreationTimestamp
  @Column(name = "created_at")
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<DocumentRelationData> relations = new HashSet<>();

  @Column(name = "search_vector", insertable = false, updatable = false, columnDefinition = "tsvector")
  @JdbcTypeCode(SqlTypes.OTHER)
  @Basic(fetch = FetchType.LAZY)
  private Object searchVector;

}
