package edu.uca.registration;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.RegistrationRepository;
import edu.uca.registration.service.RegistrationService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SystemWorkflowTest {

    static class InMemoryRepo extends RegistrationRepository {
        private final List<Student> students = new ArrayList<>();
        private final List<Course> courses = new ArrayList<>();
        public InMemoryRepo() { super("mem"); }
        @Override public List<Student> loadStudentsList() { return students; }
        @Override public List<Course> loadCoursesList() { return courses; }
        @Override public void saveStudent(Student s) { students.add(s); }
        @Override public void saveCourse(Course c) {
            courses.removeIf(x -> x.getCode().equals(c.getCode()));
            courses.add(c);
        }
    }

    @Test
    void testAddEnrollDropWorkflow() {
        InMemoryRepo repo = new InMemoryRepo();
        RegistrationService service = new RegistrationService(repo);

        // Add students & courses
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B002", "Bob", "bob@uca.edu");
        service.addCourse("CSCI4490", "Software Engineering", 1);

        // Enroll both → waitlist Bob
        service.enroll("B001", "CSCI4490");
        service.enroll("B002", "CSCI4490");

        Course c = repo.loadCoursesList().get(0);
        assertEquals(List.of("B001"), c.getRoster());
        assertEquals(List.of("B002"), c.getWaitlist());

        // Drop Alice → Bob promoted
        service.drop("B001", "CSCI4490");

        Course updated = repo.loadCoursesList().get(0);
        assertEquals(List.of("B002"), updated.getRoster());
        assertTrue(updated.getWaitlist().isEmpty());
    }
}
