package com.ekros.hl.doc.repository;

import com.ekros.hl.doc.repository.model.ProjectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectData, UUID> {

  boolean existsByProjectKey(String projectKey);
}
