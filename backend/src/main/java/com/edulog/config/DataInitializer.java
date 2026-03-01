package com.edulog.config;

import com.edulog.model.*;
import com.edulog.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, StudentRepository studentRepository,
                           SubjectRepository subjectRepository, AttendanceRepository attendanceRepository,
                           GradeRepository gradeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeRepository = gradeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // Already initialized

        // Create subjects
        Subject cs = subjectRepository.save(new Subject("Computer System", "CS101"));
        Subject da = subjectRepository.save(new Subject("Data Analysis", "DA201"));
        Subject mm = subjectRepository.save(new Subject("Multimedia and Video Production", "MM301"));
        Subject db = subjectRepository.save(new Subject("Database Management", "DB201"));
        Subject oop = subjectRepository.save(new Subject("Object Oriented Programming", "OOP201"));
        Subject pt = subjectRepository.save(new Subject("Programming Techniques", "PT101"));
        List<Subject> allSubjects = List.of(cs, da, mm, db, oop, pt);

        // Create students
        Student s1 = new Student("SC/2022/12879", "A.D.V.Chandrasekara", "Male", "1999-05-14",
                "Kandy, Sri Lanka", "chandrasekara@edu.lk", "+94712345678", "45 Batch", "CS-A");
        s1 = studentRepository.save(s1);

        Student s2 = new Student("SC/2022/12880", "Jineth Bosilu", "Male", "2000-03-22",
                "Wanduramba, Galle", "jineth@edu.lk", "+94706598632", "45 Batch", "CS-A");
        s2 = studentRepository.save(s2);

        Student s3 = new Student("SC/2022/12881", "Lal Pushpakumara", "Male", "1999-11-08",
                "Mahiyanganaya, Dambana", "lal@edu.lk", "+94706598632", "45 Batch", "CS-A");
        s3 = studentRepository.save(s3);

        Student s4 = new Student("SC/2022/12882", "Kasun Perera", "Male", "2000-01-15",
                "Colombo 07", "kasun@edu.lk", "+94771234567", "45 Batch", "CS-B");
        s4 = studentRepository.save(s4);

        Student s5 = new Student("SC/2022/12883", "Nimali Silva", "Female", "1999-07-20",
                "Negombo", "nimali@edu.lk", "+94769876543", "45 Batch", "CS-A");
        s5 = studentRepository.save(s5);

        List<Student> allStudents = List.of(s1, s2, s3, s4, s5);

        // Create admin user
        User admin = new User("admin", passwordEncoder.encode("admin123"), "Lal Pushpakumara",
                "admin@edulog.lk", Role.ADMIN);
        admin.setPhone("+94706598632");
        admin.setAddress("Mahiyanganaya, Dambana.");
        userRepository.save(admin);

        // Create teacher user
        User teacher = new User("teacher", passwordEncoder.encode("teacher123"), "Main Generator",
                "teacher@edulog.lk", Role.TEACHER);
        teacher.setPhone("+94771112233");
        teacher.setAddress("Kandy");
        userRepository.save(teacher);

        // Create student users linked to student records
        User studentUser1 = new User("jineth", passwordEncoder.encode("student123"), "Jineth Bosilu",
                "jineth@edu.lk", Role.STUDENT);
        studentUser1.setStudent(s2);
        studentUser1.setPhone("+94706598632");
        studentUser1.setAddress("Wanduramba, Galle.");
        userRepository.save(studentUser1);

        User studentUser2 = new User("lal", passwordEncoder.encode("student123"), "Lal Pushpakumara",
                "lal@edu.lk", Role.STUDENT);
        studentUser2.setStudent(s3);
        studentUser2.setPhone("+94706598632");
        studentUser2.setAddress("Mahiyanganaya, Dambana.");
        userRepository.save(studentUser2);

        User studentUser3 = new User("chandrasekara", passwordEncoder.encode("student123"), "A.D.V.Chandrasekara",
                "chandrasekara@edu.lk", Role.STUDENT);
        studentUser3.setStudent(s1);
        studentUser3.setPhone("+94712345678");
        studentUser3.setAddress("Kandy, Sri Lanka");
        userRepository.save(studentUser3);

        // Create sample grades
        String[][] gradeData = {
                {"A", "B", "C", "D", "A", "E"},  // s1
                {"A", "A", "B", "B", "A", "A"},  // s2
                {"B", "A", "B", "C", "A", "B"},  // s3
                {"C", "B", "A", "B", "C", "A"},  // s4
                {"A", "A", "A", "B", "A", "B"},  // s5
        };

        for (int i = 0; i < allStudents.size(); i++) {
            for (int j = 0; j < allSubjects.size(); j++) {
                Grade grade = new Grade(allStudents.get(i), allSubjects.get(j),
                        gradeData[i][j], "Semester 1", "admin");
                gradeRepository.save(grade);
            }
        }

        // Recalculate GPA from actual grades for each student
        for (Student student : allStudents) {
            Double gpa = gradeRepository.calculateGpaByStudentId(student.getId());
            if (gpa != null) {
                student.setGpa(Math.round(gpa * 1000.0) / 1000.0);
                studentRepository.save(student);
            }
        }

        // Create sample attendance data (last 20 days)
        Attendance.AttendanceStatus[] statuses = {
                Attendance.AttendanceStatus.PRESENT,
                Attendance.AttendanceStatus.PRESENT,
                Attendance.AttendanceStatus.PRESENT,
                Attendance.AttendanceStatus.ABSENT,
                Attendance.AttendanceStatus.PRESENT
        };

        for (int day = 0; day < 20; day++) {
            LocalDate date = LocalDate.now().minusDays(day);
            if (date.getDayOfWeek().getValue() > 5) continue; // skip weekends

            for (int i = 0; i < allStudents.size(); i++) {
                for (Subject subject : allSubjects) {
                    // Vary attendance per student
                    Attendance.AttendanceStatus status;
                    double rand = Math.random();
                    if (i == 0) status = rand < 0.6 ? Attendance.AttendanceStatus.PRESENT : Attendance.AttendanceStatus.ABSENT;
                    else if (i == 1) status = rand < 0.85 ? Attendance.AttendanceStatus.PRESENT : Attendance.AttendanceStatus.ABSENT;
                    else if (i == 2) status = rand < 0.75 ? Attendance.AttendanceStatus.PRESENT : Attendance.AttendanceStatus.ABSENT;
                    else if (i == 3) status = rand < 0.7 ? Attendance.AttendanceStatus.PRESENT : Attendance.AttendanceStatus.ABSENT;
                    else status = rand < 0.9 ? Attendance.AttendanceStatus.PRESENT : Attendance.AttendanceStatus.ABSENT;

                    attendanceRepository.save(new Attendance(allStudents.get(i), subject, date, status, "admin"));
                }
            }
        }

        System.out.println("=== EduLog Demo Data Initialized ===");
        System.out.println("Admin:   admin / admin123");
        System.out.println("Teacher: teacher / teacher123");
        System.out.println("Students: jineth / student123, lal / student123, chandrasekara / student123");
        System.out.println("=====================================");
    }
}
