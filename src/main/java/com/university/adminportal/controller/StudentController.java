package com.university.adminportal.controller;

import com.university.adminportal.config.CurrentUser;
import com.university.adminportal.model.AdvisingSession;
import com.university.adminportal.model.GeneratedDocument;
import com.university.adminportal.model.Student;
import com.university.adminportal.repository.AdvisingSessionRepository;
import com.university.adminportal.repository.GeneratedDocumentRepository;
import com.university.adminportal.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired private StudentRepository studentRepository;
    @Autowired private AdvisingSessionRepository sessionRepository;
    @Autowired private GeneratedDocumentRepository documentRepository;
    @Autowired private CurrentUser currentUser;

    @GetMapping
    public ResponseEntity<List<Student>> listStudents() {
        return ResponseEntity.ok(studentRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Map<String, Object> payload) {
        String studentId = (String) payload.get("studentId");
        if (studentId == null || studentId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "studentId is required"));
        if (studentRepository.existsByStudentId(studentId))
            return ResponseEntity.badRequest().body(Map.of("error", "Student ID already exists"));

        Student s = new Student();
        s.setStudentId(studentId);
        s.setName((String) payload.getOrDefault("name", ""));
        s.setEmail((String) payload.getOrDefault("email", ""));
        s.setProgram((String) payload.getOrDefault("program", ""));
        Object sem = payload.get("semester");
        if (sem != null) s.setSemester(Integer.parseInt(sem.toString()));
        Object gpa = payload.get("gpa");
        if (gpa != null) s.setGpa(Double.parseDouble(gpa.toString()));
        Object credits = payload.get("totalCredits");
        if (credits != null) s.setTotalCredits(Integer.parseInt(credits.toString()));
        return ResponseEntity.ok(studentRepository.save(s));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudent(@PathVariable String studentId) {
        return studentRepository.findByStudentId(studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<?> updateStudent(@PathVariable String studentId,
                                           @RequestBody Map<String, Object> payload) {
        Optional<Student> opt = studentRepository.findByStudentId(studentId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Student s = opt.get();
        if (payload.containsKey("name"))         s.setName((String) payload.get("name"));
        if (payload.containsKey("email"))        s.setEmail((String) payload.get("email"));
        if (payload.containsKey("program"))      s.setProgram((String) payload.get("program"));
        if (payload.containsKey("semester"))     s.setSemester(Integer.parseInt(payload.get("semester").toString()));
        if (payload.containsKey("gpa"))          s.setGpa(Double.parseDouble(payload.get("gpa").toString()));
        if (payload.containsKey("totalCredits")) s.setTotalCredits(Integer.parseInt(payload.get("totalCredits").toString()));
        return ResponseEntity.ok(studentRepository.save(s));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteStudent(@PathVariable String studentId) {
        Optional<Student> opt = studentRepository.findByStudentId(studentId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        studentRepository.delete(opt.get());
        return ResponseEntity.ok(Map.of("message", "Student deleted"));
    }

    // My advising sessions (logged-in user)
    @GetMapping("/me/sessions")
    public ResponseEntity<List<AdvisingSession>> getMySessions() {
        return ResponseEntity.ok(sessionRepository.findByStudentIdOrderByCreatedAtDesc(currentUser.getEmail()));
    }

    // My documents (logged-in user)
    @GetMapping("/me/documents")
    public ResponseEntity<List<GeneratedDocument>> getMyDocuments() {
        return ResponseEntity.ok(documentRepository.findByStudentIdOrderByCreatedAtDesc(currentUser.getEmail()));
    }

    // Sessions for any student ID
    @GetMapping("/{studentId}/sessions")
    public ResponseEntity<List<AdvisingSession>> getSessions(@PathVariable String studentId) {
        return ResponseEntity.ok(sessionRepository.findByStudentIdOrderByCreatedAtDesc(studentId));
    }

    // Documents for any student ID
    @GetMapping("/{studentId}/documents")
    public ResponseEntity<List<GeneratedDocument>> getDocuments(@PathVariable String studentId) {
        return ResponseEntity.ok(documentRepository.findByStudentIdOrderByCreatedAtDesc(studentId));
    }
}
