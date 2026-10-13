package com.ekros.hl.doc.repository;

import com.ekros.hl.doc.repository.model.DocumentData;
import com.ekros.hl.doc.repository.projection.ProjectDocumentsCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentData, UUID>, JpaSpecificationExecutor<DocumentData> {

  @Query("SELECT d.projectKey AS projectKey, COUNT(d) AS count " +
      "FROM DocumentData d " +
      "WHERE d.projectKey IN :projectKeys " +
      "GROUP BY d.projectKey")
  List<ProjectDocumentsCountProjection> countBySpecificProjectKeys(@Param("projectKeys") Collection<String> projectKeys);
}
