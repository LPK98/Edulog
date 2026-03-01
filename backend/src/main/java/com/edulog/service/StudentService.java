package com.edulog.service;

import com.edulog.model.Student;
import com.edulog.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    public Student findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
    }

    public List<Student> search(String query) {
        return studentRepository.findByNameContainingIgnoreCase(query);
    }

    public Student create(Student student) {
        if (studentRepository.existsByStudentId(student.getStudentId())) {
            throw new RuntimeException("Student ID already exists: " + student.getStudentId());
        }
        return studentRepository.save(student);
    }

    public Student update(Long id, Student updated) {
        Student student = findById(id);
        student.setName(updated.getName());
        student.setGender(updated.getGender());
        student.setDob(updated.getDob());
        student.setAddress(updated.getAddress());
        student.setEmail(updated.getEmail());
        student.setPhone(updated.getPhone());
        student.setBatch(updated.getBatch());
        student.setClassName(updated.getClassName());
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
    }

    public long count() {
        return studentRepository.count();
    }
}
