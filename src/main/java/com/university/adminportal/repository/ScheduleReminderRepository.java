package com.university.adminportal.repository;

import com.university.adminportal.model.ScheduleReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleReminderRepository extends JpaRepository<ScheduleReminder, Long> {
    List<ScheduleReminder> findByStudentIdOrderByDueDateAsc(String studentId);
    List<ScheduleReminder> findAllByOrderByDueDateAsc();
}
