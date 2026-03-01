package com.edulog.service;

import com.edulog.model.Grade;
import com.edulog.model.Student;
import com.edulog.model.Subject;
import com.edulog.repository.AttendanceRepository;
import com.edulog.repository.GradeRepository;
import com.edulog.repository.StudentRepository;
import com.edulog.repository.SubjectRepository;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    // ── Color Palette ──
    private static final DeviceRgb PRIMARY     = new DeviceRgb(0, 212, 255);    // Cyan accent
    private static final DeviceRgb PRIMARY_DARK= new DeviceRgb(0, 170, 204);    // Darker cyan
    private static final DeviceRgb SECONDARY   = new DeviceRgb(124, 58, 237);   // Purple
    private static final DeviceRgb DARK_BG     = new DeviceRgb(15, 15, 25);     // Near-black
    private static final DeviceRgb CARD_BG     = new DeviceRgb(22, 22, 35);     // Card dark
    private static final DeviceRgb HEADER_BG   = new DeviceRgb(18, 18, 30);     // Header bg
    private static final DeviceRgb ROW_ALT     = new DeviceRgb(25, 25, 40);     // Alternating row
    private static final DeviceRgb ROW_DEFAULT = new DeviceRgb(18, 18, 32);     // Default row
    private static final DeviceRgb TEXT_WHITE  = new DeviceRgb(226, 232, 240);   // Light text
    private static final DeviceRgb TEXT_MUTED  = new DeviceRgb(148, 163, 184);  // Muted text
    private static final DeviceRgb TEXT_DIM    = new DeviceRgb(100, 116, 139);  // Dim text
    private static final DeviceRgb BORDER_CLR  = new DeviceRgb(40, 40, 60);     // Subtle border
    private static final DeviceRgb SUCCESS     = new DeviceRgb(34, 197, 94);    // Green
    private static final DeviceRgb WARNING     = new DeviceRgb(245, 158, 11);   // Amber
    private static final DeviceRgb DANGER      = new DeviceRgb(239, 68, 68);    // Red

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
            pdf.setDefaultPageSize(PageSize.A4);
            Document doc = new Document(pdf, PageSize.A4);
            doc.setMargins(0, 0, 0, 0);

            // ═══════════ DARK PAGE BACKGROUND ═══════════
            addPageBackground(pdf);

            // Set content margins after background
            doc.setMargins(30, 40, 40, 40);

            // ═══════════ HEADER BANNER ═══════════
            addHeaderBanner(doc, student);

            // ═══════════ STUDENT INFO CARD ═══════════
            addStudentInfoCard(doc, student);

            // ═══════════ SUMMARY STATS ROW ═══════════
            addSummaryStats(doc, gpa, grades, studentId);

            // ═══════════ GRADES TABLE ═══════════
            if (!grades.isEmpty()) {
                addGradesSection(doc, grades);
            }

            // ═══════════ ATTENDANCE SECTION ═══════════
            List<Subject> subjects = subjectRepository.findAll();
            if (!subjects.isEmpty()) {
                addAttendanceSection(doc, subjects, studentId);
            }

            // ═══════════ FOOTER ═══════════
            addFooter(doc);

            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }

        return baos.toByteArray();
    }

    // ──────────────────────────────────────────────────────────
    // PAGE BACKGROUND
    // ──────────────────────────────────────────────────────────
    private void addPageBackground(PdfDocument pdf) {
        var page = pdf.addNewPage();
        var canvas = new com.itextpdf.kernel.pdf.canvas.PdfCanvas(page);
        canvas.saveState();
        canvas.setFillColor(DARK_BG);
        canvas.rectangle(0, 0, PageSize.A4.getWidth(), PageSize.A4.getHeight());
        canvas.fill();
        canvas.restoreState();
    }

    // ──────────────────────────────────────────────────────────
    // HEADER BANNER
    // ──────────────────────────────────────────────────────────
    private void addHeaderBanner(Document doc, Student student) {
        Table banner = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(HEADER_BG)
                .setBorder(new SolidBorder(BORDER_CLR, 1))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));

        // Left: Branding
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setPadding(20);
        leftCell.add(new Paragraph("EDU").setFontSize(28).setBold().setFontColor(PRIMARY)
                .add(new com.itextpdf.layout.element.Text("LOG").setFontColor(TEXT_WHITE)));
        leftCell.add(new Paragraph("STUDENT ACADEMIC REPORT")
                .setFontSize(9).setFontColor(TEXT_DIM).setCharacterSpacing(3).setMarginTop(-4));
        banner.addCell(leftCell);

        // Right: Date & ID
        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setPadding(20)
                .setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE);
        rightCell.add(new Paragraph("Report Date").setFontSize(8).setFontColor(TEXT_DIM).setCharacterSpacing(1.5f));
        rightCell.add(new Paragraph(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
                .setFontSize(11).setFontColor(TEXT_WHITE).setBold().setMarginTop(2));
        rightCell.add(new Paragraph("ID: " + student.getStudentId())
                .setFontSize(9).setFontColor(PRIMARY).setMarginTop(4));
        banner.addCell(rightCell);

        doc.add(banner);
        doc.add(spacer(16));
    }

    // ──────────────────────────────────────────────────────────
    // STUDENT INFO CARD
    // ──────────────────────────────────────────────────────────
    private void addStudentInfoCard(Document doc, Student student) {
        doc.add(sectionTitle("Personal Information"));

        Table card = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(CARD_BG)
                .setBorder(new SolidBorder(BORDER_CLR, 1))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));

        addInfoCell(card, "Full Name", student.getName());
        addInfoCell(card, "Student ID", student.getStudentId());
        addInfoCell(card, "Gender", student.getGender() != null ? student.getGender() : "—");
        addInfoCell(card, "Date of Birth", student.getDob() != null ? student.getDob() : "—");
        addInfoCell(card, "Batch", student.getBatch() != null ? student.getBatch() : "—");
        addInfoCell(card, "Email", student.getEmail() != null ? student.getEmail() : "—");
        addInfoCell(card, "Phone", student.getPhone() != null ? student.getPhone() : "—");
        addInfoCell(card, "Address", student.getAddress() != null ? student.getAddress() : "—");

        doc.add(card);
        doc.add(spacer(16));
    }

    // ──────────────────────────────────────────────────────────
    // SUMMARY STATS
    // ──────────────────────────────────────────────────────────
    private void addSummaryStats(Document doc, Double gpa, List<Grade> grades, Long studentId) {
        int totalSubjects = grades.size();
        String bestGrade = getBestGrade(grades);

        // Calculate average attendance
        List<Subject> subjects = subjectRepository.findAll();
        double avgAtt = 0;
        if (!subjects.isEmpty()) {
            double totalPct = 0;
            int count = 0;
            for (Subject s : subjects) {
                long total = attendanceRepository.countTotalByStudentAndSubject(studentId, s.getId());
                long present = attendanceRepository.countPresentByStudentAndSubject(studentId, s.getId());
                if (total > 0) {
                    totalPct += (double) present / total * 100;
                    count++;
                }
            }
            avgAtt = count > 0 ? totalPct / count : 0;
        }

        Table stats = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        addStatCard(stats, "GPA", gpa != null ? String.format("%.2f", gpa) : "N/A", PRIMARY);
        addStatCard(stats, "SUBJECTS", String.valueOf(totalSubjects), SECONDARY);
        addStatCard(stats, "BEST GRADE", bestGrade, SUCCESS);
        addStatCard(stats, "ATTENDANCE", String.format("%.0f%%", avgAtt), getAttendanceColor(avgAtt));

        doc.add(stats);
        doc.add(spacer(16));
    }

    // ──────────────────────────────────────────────────────────
    // GRADES TABLE
    // ──────────────────────────────────────────────────────────
    private void addGradesSection(Document doc, List<Grade> grades) {
        doc.add(sectionTitle("Academic Grades"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{0.5f, 3, 1, 1, 1.2f}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(BORDER_CLR, 1))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));

        // Header row
        addTableHeader(table, "#");
        addTableHeader(table, "SUBJECT");
        addTableHeader(table, "GRADE");
        addTableHeader(table, "POINTS");
        addTableHeader(table, "SEMESTER");

        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            DeviceRgb rowBg = (i % 2 == 0) ? ROW_DEFAULT : ROW_ALT;

            table.addCell(dataCell(String.valueOf(i + 1), rowBg, TextAlignment.CENTER, TEXT_DIM, false));
            table.addCell(dataCell(g.getSubject().getName(), rowBg, TextAlignment.LEFT, TEXT_WHITE, true));
            table.addCell(gradeBadgeCell(g.getGrade(), rowBg));
            table.addCell(dataCell(String.valueOf(g.getGradePoint()), rowBg, TextAlignment.CENTER, PRIMARY, true));
            table.addCell(dataCell(g.getSemester() != null ? g.getSemester() : "—", rowBg, TextAlignment.CENTER, TEXT_MUTED, false));
        }

        doc.add(table);
        doc.add(spacer(16));
    }

    // ──────────────────────────────────────────────────────────
    // ATTENDANCE SECTION
    // ──────────────────────────────────────────────────────────
    private void addAttendanceSection(Document doc, List<Subject> subjects, Long studentId) {
        doc.add(sectionTitle("Attendance Summary"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1.2f, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(BORDER_CLR, 1))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));

        addTableHeader(table, "SUBJECT");
        addTableHeader(table, "PRESENT");
        addTableHeader(table, "TOTAL");
        addTableHeader(table, "PERCENTAGE");
        addTableHeader(table, "STATUS");

        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subject.getId());
            long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subject.getId());
            double pct = total > 0 ? (double) present / total * 100 : 0;
            DeviceRgb rowBg = (i % 2 == 0) ? ROW_DEFAULT : ROW_ALT;
            DeviceRgb pctColor = getAttendanceColor(pct);
            String status = pct >= 75 ? "Good" : pct >= 50 ? "Warning" : "Low";

            table.addCell(dataCell(subject.getName(), rowBg, TextAlignment.LEFT, TEXT_WHITE, true));
            table.addCell(dataCell(String.valueOf(present), rowBg, TextAlignment.CENTER, SUCCESS, true));
            table.addCell(dataCell(String.valueOf(total), rowBg, TextAlignment.CENTER, TEXT_MUTED, false));
            table.addCell(dataCell(String.format("%.1f%%", pct), rowBg, TextAlignment.CENTER, pctColor, true));
            table.addCell(statusBadgeCell(status, rowBg));
        }

        doc.add(table);
        doc.add(spacer(16));
    }

    // ──────────────────────────────────────────────────────────
    // FOOTER
    // ──────────────────────────────────────────────────────────
    private void addFooter(Document doc) {
        SolidLine line = new SolidLine(0.5f);
        line.setColor(BORDER_CLR);
        doc.add(new LineSeparator(line).setMarginTop(8));

        Table footer = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginTop(8);

        Cell left = new Cell().setBorder(Border.NO_BORDER);
        left.add(new Paragraph("EduLog Academic Management System")
                .setFontSize(8).setFontColor(TEXT_DIM));
        left.add(new Paragraph("This is a system-generated report.")
                .setFontSize(7).setFontColor(TEXT_DIM).setMarginTop(2));
        footer.addCell(left);

        Cell right = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        right.add(new Paragraph("Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .setFontSize(8).setFontColor(TEXT_DIM));
        right.add(new Paragraph("© " + LocalDate.now().getYear() + " EduLog")
                .setFontSize(7).setFontColor(PRIMARY).setMarginTop(2));
        footer.addCell(right);

        doc.add(footer);
    }

    // ══════════════════════════════════════════════════════════
    //  HELPER METHODS
    // ══════════════════════════════════════════════════════════

    private Paragraph sectionTitle(String title) {
        return new Paragraph(title)
                .setFontSize(13).setBold().setFontColor(PRIMARY)
                .setCharacterSpacing(1.5f).setMarginBottom(8);
    }

    private Paragraph spacer(float height) {
        return new Paragraph("").setMarginBottom(height);
    }

    private void addInfoCell(Table table, String label, String value) {
        Cell cell = new Cell().setBorder(Border.NO_BORDER).setPadding(10).setPaddingLeft(16);
        cell.add(new Paragraph(label).setFontSize(8).setFontColor(TEXT_DIM).setCharacterSpacing(1));
        cell.add(new Paragraph(value != null ? value : "—")
                .setFontSize(11).setFontColor(TEXT_WHITE).setBold().setMarginTop(2));
        table.addCell(cell);
    }

    private void addStatCard(Table table, String label, String value, DeviceRgb accentColor) {
        Cell cell = new Cell().setBorder(new SolidBorder(BORDER_CLR, 1))
                .setBackgroundColor(CARD_BG).setPadding(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8));
        cell.add(new Paragraph(label).setFontSize(8).setFontColor(TEXT_DIM).setCharacterSpacing(2));
        cell.add(new Paragraph(value).setFontSize(22).setBold().setFontColor(accentColor).setMarginTop(4));
        table.addCell(cell);
    }

    private void addTableHeader(Table table, String text) {
        Cell cell = new Cell().setBackgroundColor(HEADER_BG)
                .setBorderBottom(new SolidBorder(PRIMARY, 2))
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setPadding(10);
        cell.add(new Paragraph(text).setFontSize(9).setBold().setFontColor(PRIMARY).setCharacterSpacing(1.5f)
                .setTextAlignment(TextAlignment.CENTER));
        table.addHeaderCell(cell);
    }

    private Cell dataCell(String text, DeviceRgb bgColor, TextAlignment align, DeviceRgb textColor, boolean bold) {
        Cell cell = new Cell().setBackgroundColor(bgColor)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(BORDER_CLR, 0.5f))
                .setPadding(9);
        Paragraph p = new Paragraph(text).setFontSize(10).setFontColor(textColor).setTextAlignment(align);
        if (bold) p.setBold();
        cell.add(p);
        return cell;
    }

    private Cell gradeBadgeCell(String grade, DeviceRgb rowBg) {
        DeviceRgb color = getGradeColor(grade);
        Cell cell = new Cell().setBackgroundColor(rowBg)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(BORDER_CLR, 0.5f))
                .setPadding(9).setTextAlignment(TextAlignment.CENTER);
        cell.add(new Paragraph(grade).setFontSize(11).setBold().setFontColor(color));
        return cell;
    }

    private Cell statusBadgeCell(String status, DeviceRgb rowBg) {
        DeviceRgb color;
        if ("Good".equals(status)) color = SUCCESS;
        else if ("Warning".equals(status)) color = WARNING;
        else color = DANGER;

        Cell cell = new Cell().setBackgroundColor(rowBg)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(BORDER_CLR, 0.5f))
                .setPadding(9).setTextAlignment(TextAlignment.CENTER);
        cell.add(new Paragraph(status).setFontSize(9).setBold().setFontColor(color).setCharacterSpacing(0.5f));
        return cell;
    }

    private DeviceRgb getGradeColor(String grade) {
        if (grade == null) return TEXT_MUTED;
        if (grade.startsWith("A")) return SUCCESS;
        if (grade.startsWith("B")) return PRIMARY;
        if (grade.startsWith("C")) return WARNING;
        return DANGER;
    }

    private DeviceRgb getAttendanceColor(double pct) {
        if (pct >= 85) return SUCCESS;
        if (pct >= 70) return PRIMARY;
        if (pct >= 50) return WARNING;
        return DANGER;
    }

    private String getBestGrade(List<Grade> grades) {
        if (grades.isEmpty()) return "—";
        String[] order = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F"};
        String best = "F";
        int bestIdx = order.length - 1;
        for (Grade g : grades) {
            for (int i = 0; i < order.length; i++) {
                if (order[i].equals(g.getGrade()) && i < bestIdx) {
                    bestIdx = i;
                    best = order[i];
                }
            }
        }
        return best;
    }
}
