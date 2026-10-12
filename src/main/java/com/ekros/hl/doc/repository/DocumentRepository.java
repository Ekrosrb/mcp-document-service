package com.ekros.hl.doc.repository;

import com.ekros.hl.doc.repository.model.DocumentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentData, UUID>, JpaSpecificationExecutor<DocumentData> {

}
