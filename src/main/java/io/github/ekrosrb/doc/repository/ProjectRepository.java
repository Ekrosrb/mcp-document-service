package io.github.ekrosrb.doc.repository;

import io.github.ekrosrb.doc.repository.model.ProjectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends JpaRepository<ProjectData, Long> {

  /**
   * Creates the project unless one with this key exists; returns the number of created rows.
   */
  @Modifying
  @Query(value = "INSERT INTO project (project_key) VALUES (:projectKey) ON CONFLICT (project_key) DO NOTHING",
      nativeQuery = true)
  int insertIfAbsent(@Param("projectKey") String projectKey);
}
