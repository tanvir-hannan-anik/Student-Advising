package com.university.adminportal.repository;

import com.university.adminportal.model.AdvisingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvisingSessionRepository extends JpaRepository<AdvisingSession, Long> {
    List<AdvisingSession> findByStudentIdOrderByCreatedAtDesc(String studentId);
    List<AdvisingSession> findTop20ByOrderByCreatedAtDesc();
}
