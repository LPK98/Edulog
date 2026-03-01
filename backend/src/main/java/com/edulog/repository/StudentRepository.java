package com.edulog.repository;

import com.edulog.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    List<Student> findByNameContainingIgnoreCase(String name);
    List<Student> findByBatch(String batch);
    boolean existsByStudentId(String studentId);
}
