package com.edulog.repository;

import com.edulog.model.Attendance;
import com.edulog.model.Student;
import com.edulog.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByStudentAndSubject(Student student, Subject subject);
    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);

    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(Long studentId, Long subjectId);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId")
    long countTotalByStudentAndSubject(Long studentId, Long subjectId);
}
