package com.university.adminportal.repository;

import com.university.adminportal.model.CommunicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationRecordRepository extends JpaRepository<CommunicationRecord, Long> {
    List<CommunicationRecord> findByStudentIdOrderByCreatedAtDesc(String studentId);
    List<CommunicationRecord> findTop20ByOrderByCreatedAtDesc();
}
