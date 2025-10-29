package edu.uca.registration.service;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.RegistrationRepository;
import edu.uca.registration.util.EnrollmentException;
import edu.uca.registration.util.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    private RegistrationService service;
    private MockRepo repo;

    static class MockRepo extends RegistrationRepository {
        private final List<Student> students = new ArrayList<>();
        private final List<Course> courses = new ArrayList<>();

        public MockRepo() { super("mock"); }

        @Override
        public List<Student> loadStudentsList() { return students; }

        @Override
        public void saveStudent(Student s) { students.add(s); }

        @Override
        public List<Course> loadCoursesList() { return courses; }

        @Override
        public void saveCourse(Course c) {
            courses.removeIf(existing -> existing.getCode().equals(c.getCode()));
            courses.add(c);
        }
    }

    @BeforeEach
    void setup() {
        repo = new MockRepo();
        service = new RegistrationService(repo);
    }

    @Test
    void testAddAndRetrieveStudent() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        assertEquals(1, service.allStudents().size());
        assertEquals("Alice", service.allStudents().iterator().next().getName());
    }

    @Test
    void duplicateStudentShouldThrow() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        assertThrows(ValidationException.class, () ->
                service.addStudent("B001", "Bob", "bob@uca.edu"));
    }

    @Test
    void testEnrollWaitlistPromotion() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B002", "Bob", "bob@uca.edu");
        service.addCourse("CSCI4490", "Software Engineering", 1);

        service.enroll("B001", "CSCI4490");
        service.enroll("B002", "CSCI4490");

        Course c = repo.loadCoursesList().get(0);
        assertEquals(List.of("B001"), c.getRoster());
        assertEquals(List.of("B002"), c.getWaitlist());

        service.drop("B001", "CSCI4490");
        Course updated = repo.loadCoursesList().get(0);
        assertEquals(List.of("B002"), updated.getRoster());
        assertTrue(updated.getWaitlist().isEmpty());
    }

    @Test
    void enrollNonexistentStudentShouldThrow() {
        service.addCourse("CSCI1000", "Intro", 1);
        assertThrows(EnrollmentException.class, () ->
                service.enroll("B999", "CSCI1000"));
    }
}
