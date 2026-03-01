package com.edulog.repository;

import com.edulog.model.Grade;
import com.edulog.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent(Student student);
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByStudentIdAndSemester(Long studentId, String semester);

    @Query("SELECT AVG(g.gradePoint) FROM Grade g WHERE g.student.id = :studentId")
    Double calculateGpaByStudentId(Long studentId);
}
