package com.university.adminportal.repository;

import com.university.adminportal.model.GeneratedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, Long> {
    List<GeneratedDocument> findByStudentIdOrderByCreatedAtDesc(String studentId);
    List<GeneratedDocument> findTop20ByOrderByCreatedAtDesc();
}
