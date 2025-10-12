package edu.uca.registration.service;

import java.util.Collection;
import java.util.List;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.RegistrationRepository;
import edu.uca.registration.util.EnrollmentException;
import edu.uca.registration.util.ValidationException;

public class RegistrationService {

    private final RegistrationRepository repo;

    public RegistrationService(String baseDir) {
        this(new RegistrationRepository(baseDir));
    }

    public RegistrationService(RegistrationRepository repo) {
        this.repo = repo;
    }

    // -------------------- Students --------------------
    public void addStudent(String id, String name, String email) {
        List<Student> students = repo.loadStudentsList();
        for (Student s : students) {
            if (s.getId().equals(id)) throw new ValidationException("Duplicate student ID.");
        }
        repo.saveStudent(new Student(id, name, email));
    }

    public Collection<Student> allStudents() {
        return repo.loadStudentsList();
    }

    // -------------------- Courses --------------------
    public void addCourse(String code, String title, int capacity) {
        List<Course> courses = repo.loadCoursesList();
        for (Course c : courses) {
            if (c.getCode().equals(code)) throw new ValidationException("Duplicate course code.");
        }
        repo.saveCourse(new Course(code, title, capacity));
    }

    public Collection<Course> allCourses() {
        return repo.loadCoursesList();
    }

    // -------------------- Enrollment --------------------
    public void enroll(String studentId, String courseCode) {
        if (allStudents().stream().noneMatch(s -> s.getId().equals(studentId))) {
            throw new EnrollmentException("No such student: " + studentId);
        }
        List<Course> courses = repo.loadCoursesList();
        Course course = courses.stream().filter(c -> c.getCode().equals(courseCode))
                .findFirst().orElseThrow(() -> new EnrollmentException("No such course: " + courseCode));

        List<String> roster = course.getRoster();
        List<String> waitlist = course.getWaitlist();

        if (roster.contains(studentId)) throw new EnrollmentException("Already enrolled.");
        if (waitlist.contains(studentId)) throw new EnrollmentException("Already waitlisted.");

        if (roster.size() >= course.getCapacity()) {
            waitlist.add(studentId);
        } else {
            roster.add(studentId);
        }
        repo.saveCourse(course);
    }

    public void drop(String studentId, String courseCode) {
        List<Course> courses = repo.loadCoursesList();
        Course course = courses.stream().filter(c -> c.getCode().equals(courseCode))
                .findFirst().orElseThrow(() -> new EnrollmentException("No such course: " + courseCode));

        List<String> roster = course.getRoster();
        List<String> waitlist = course.getWaitlist();

        if (roster.remove(studentId)) {
            // Promote from waitlist
            if (!waitlist.isEmpty()) {
                roster.add(waitlist.remove(0));
            }
        } else if (!waitlist.remove(studentId)) {
            throw new EnrollmentException("Student not enrolled or waitlisted.");
        }
        repo.saveCourse(course);
    }

    // -------------------- Demo data --------------------
    public void seedDemoData() {
        try {
            addStudent("B001", "Alice", "alice@uca.edu");
            addStudent("B002", "Brian", "brian@uca.edu");
            addCourse("CSCI4490", "Software Engineering", 2);
            addCourse("MATH1496", "Calculus I", 50);
        } catch (Exception ignored) {}
    }
}