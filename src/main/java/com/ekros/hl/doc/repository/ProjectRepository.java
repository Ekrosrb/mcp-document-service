package com.ekros.hl.doc.repository;

import com.ekros.hl.doc.repository.model.ProjectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends JpaRepository<ProjectData, Long> {

  boolean existsByProjectKey(String projectKey);
}
