package edu.uca.registration.repo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.util.EnrollmentException;

public class RegistrationRepository {
    private final String baseDir;
    private final Logger logger = Logger.getLogger(RegistrationRepository.class.getName());

    private final Map<String, Student> students = new LinkedHashMap<>();
    private final Map<String, Course> courses = new LinkedHashMap<>();

    private final String STUDENTS_CSV = "students.csv";
    private final String COURSES_CSV = "courses.csv";
    private final String ENROLLMENTS_CSV = "enrollments.csv";

    public RegistrationRepository(String baseDir) {
        this.baseDir = baseDir;
        loadAll();
    }

    // -------------------- Student --------------------
    public void saveStudent(Student s) {
        students.put(s.getId(), s);
        saveStudents();
    }

    public List<Student> loadStudentsList() {
        return new ArrayList<>(students.values());
    }

    // -------------------- Course --------------------
    public void saveCourse(Course c) {
        courses.put(c.getCode(), c);
        saveCourses();
    }

    public List<Course> loadCoursesList() {
        return new ArrayList<>(courses.values());
    }

    // -------------------- Enrollment --------------------
    public void enroll(String studentId, String courseCode) {
        Course c = courses.get(courseCode);
        if (c == null) throw new EnrollmentException("No such course: " + courseCode);

        if (c.getRoster().contains(studentId)) throw new EnrollmentException("Already enrolled");
        if (c.getWaitlist().contains(studentId)) throw new EnrollmentException("Already waitlisted");

        if (c.getRoster().size() >= c.getCapacity()) {
            c.getWaitlist().add(studentId);
            logger.info("Waitlisted " + studentId + " in " + courseCode);
        } else {
            c.getRoster().add(studentId);
            logger.info("Enrolled " + studentId + " in " + courseCode);
        }
        saveEnrollments();
    }

    public void drop(String studentId, String courseCode) {
        Course c = courses.get(courseCode);
        if (c == null) throw new EnrollmentException("No such course: " + courseCode);

        if (c.getRoster().remove(studentId)) {
            // Promote first waitlisted (FIFO)
            if (!c.getWaitlist().isEmpty()) {
                String promote = c.getWaitlist().remove(0);
                c.getRoster().add(promote);
                logger.info("Promoted " + promote + " to roster in " + courseCode);
            }
        } else if (!c.getWaitlist().remove(studentId)) {
            throw new EnrollmentException("Student not enrolled or waitlisted");
        }
        saveEnrollments();
    }

    // -------------------- Persistence --------------------
    private void loadAll() {
        loadStudents();
        loadCourses();
        loadEnrollments();
    }

    private void saveAll() {
        saveStudents();
        saveCourses();
        saveEnrollments();
    }

    private void loadStudents() {
        File f = new File(baseDir, STUDENTS_CSV);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    students.put(p[0], new Student(p[0], p[1], p[2]));
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to load students: " + e.getMessage());
        }
    }

    private void saveStudents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(new File(baseDir, STUDENTS_CSV)))) {
            for (Student s : students.values()) {
                pw.println(s.getId() + "," + s.getName() + "," + s.getEmail());
            }
        } catch (Exception e) {
            logger.warning("Failed to save students: " + e.getMessage());
        }
    }

    private void loadCourses() {
        File f = new File(baseDir, COURSES_CSV);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 3) {
                    try {
                        int cap = Integer.parseInt(p[2]);
                        courses.put(p[0], new Course(p[0], p[1], cap));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to load courses: " + e.getMessage());
        }
    }

    private void saveCourses() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(new File(baseDir, COURSES_CSV)))) {
            for (Course c : courses.values()) {
                pw.println(c.getCode() + "," + c.getTitle() + "," + c.getCapacity());
            }
        } catch (Exception e) {
            logger.warning("Failed to save courses: " + e.getMessage());
        }
    }

    private void loadEnrollments() {
        File f = new File(baseDir, ENROLLMENTS_CSV);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                if (p.length >= 3) {
                    Course c = courses.get(p[0]);
                    if (c == null) continue;
                    String sid = p[1], status = p[2];
                    if ("ENROLLED".equalsIgnoreCase(status)) c.getRoster().add(sid);
                    else if ("WAITLIST".equalsIgnoreCase(status)) c.getWaitlist().add(sid);
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to load enrollments: " + e.getMessage());
        }
    }

    private void saveEnrollments() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(new File(baseDir, ENROLLMENTS_CSV)))) {
            for (Course c : courses.values()) {
                for (String sid : c.getRoster()) pw.println(c.getCode() + "|" + sid + "|ENROLLED");
                for (String sid : c.getWaitlist()) pw.println(c.getCode() + "|" + sid + "|WAITLIST");
            }
        } catch (Exception e) {
            logger.warning("Failed to save enrollments: " + e.getMessage());
        }
    }
}