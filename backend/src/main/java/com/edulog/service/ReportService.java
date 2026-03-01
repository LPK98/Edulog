package com.edulog.service;

import com.edulog.model.Grade;
import com.edulog.model.Student;
import com.edulog.model.Subject;
import com.edulog.repository.AttendanceRepository;
import com.edulog.repository.GradeRepository;
import com.edulog.repository.StudentRepository;
import com.edulog.repository.SubjectRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final SubjectRepository subjectRepository;

    public ReportService(StudentRepository studentRepository, GradeRepository gradeRepository,
                         AttendanceRepository attendanceRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
        this.subjectRepository = subjectRepository;
    }

    public byte[] generateStudentReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        Double gpa = gradeRepository.calculateGpaByStudentId(studentId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            DeviceRgb orange = new DeviceRgb(255, 154, 32);
            DeviceRgb darkGray = new DeviceRgb(47, 49, 51);

            // Title
            document.add(new Paragraph("EDULOG - Student Report")
                    .setFontSize(24).setBold().setFontColor(darkGray)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            // Student Info
            document.add(new Paragraph("Student Information")
                    .setFontSize(16).setBold().setFontColor(orange).setMarginBottom(10));

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(60));
            addInfoRow(infoTable, "Student ID", student.getStudentId());
            addInfoRow(infoTable, "Name", student.getName());
            addInfoRow(infoTable, "Gender", student.getGender() != null ? student.getGender() : "-");
            addInfoRow(infoTable, "Date of Birth", student.getDob() != null ? student.getDob() : "-");
            addInfoRow(infoTable, "Batch", student.getBatch() != null ? student.getBatch() : "-");
            addInfoRow(infoTable, "Phone", student.getPhone() != null ? student.getPhone() : "-");
            addInfoRow(infoTable, "Address", student.getAddress() != null ? student.getAddress() : "-");
            document.add(infoTable);

            // GPA
            document.add(new Paragraph("\nOverall GPA: " + (gpa != null ? String.format("%.3f", gpa) : "N/A"))
                    .setFontSize(18).setBold().setFontColor(darkGray).setMarginTop(20));

            // Grades Table
            if (!grades.isEmpty()) {
                document.add(new Paragraph("\nGrade Report")
                        .setFontSize(16).setBold().setFontColor(orange).setMarginTop(15));

                Table gradeTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1}))
                        .setWidth(UnitValue.createPercentValue(100));

                // Header
                gradeTable.addHeaderCell(createHeaderCell("Subject"));
                gradeTable.addHeaderCell(createHeaderCell("Grade"));
                gradeTable.addHeaderCell(createHeaderCell("Grade Point"));
                gradeTable.addHeaderCell(createHeaderCell("Semester"));

                for (Grade grade : grades) {
                    gradeTable.addCell(new Cell().add(new Paragraph(grade.getSubject().getName())));
                    gradeTable.addCell(new Cell().add(new Paragraph(grade.getGrade()))
                            .setTextAlignment(TextAlignment.CENTER));
                    gradeTable.addCell(new Cell().add(new Paragraph(String.valueOf(grade.getGradePoint())))
                            .setTextAlignment(TextAlignment.CENTER));
                    gradeTable.addCell(new Cell().add(new Paragraph(
                            grade.getSemester() != null ? grade.getSemester() : "-"))
                            .setTextAlignment(TextAlignment.CENTER));
                }
                document.add(gradeTable);
            }

            // Attendance Summary
            List<Subject> subjects = subjectRepository.findAll();
            if (!subjects.isEmpty()) {
                document.add(new Paragraph("\nAttendance Summary")
                        .setFontSize(16).setBold().setFontColor(orange).setMarginTop(15));

                Table attTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1}))
                        .setWidth(UnitValue.createPercentValue(100));

                attTable.addHeaderCell(createHeaderCell("Subject"));
                attTable.addHeaderCell(createHeaderCell("Present"));
                attTable.addHeaderCell(createHeaderCell("Total"));
                attTable.addHeaderCell(createHeaderCell("Percentage"));

                for (Subject subject : subjects) {
                    long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subject.getId());
                    long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subject.getId());
                    double pct = total > 0 ? (double) present / total * 100 : 0;

                    attTable.addCell(new Cell().add(new Paragraph(subject.getName())));
                    attTable.addCell(new Cell().add(new Paragraph(String.valueOf(present)))
                            .setTextAlignment(TextAlignment.CENTER));
                    attTable.addCell(new Cell().add(new Paragraph(String.valueOf(total)))
                            .setTextAlignment(TextAlignment.CENTER));
                    attTable.addCell(new Cell().add(new Paragraph(String.format("%.1f%%", pct)))
                            .setTextAlignment(TextAlignment.CENTER));
                }
                document.add(attTable);
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }

        return baos.toByteArray();
    }

    private void addInfoRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold())
                .setBorder(null).setPaddingBottom(4));
        table.addCell(new Cell().add(new Paragraph(value))
                .setBorder(null).setPaddingBottom(4));
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(new DeviceRgb(47, 49, 51))
                .setTextAlignment(TextAlignment.CENTER);
    }
}
