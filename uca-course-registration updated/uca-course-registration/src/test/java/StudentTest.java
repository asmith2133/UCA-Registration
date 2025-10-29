package edu.uca.registration.model;

import edu.uca.registration.util.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void validStudentShouldPassValidation() {
        Student s = new Student("B001", "Alice", "alice@uca.edu");
        assertEquals("B001", s.getId());
        assertEquals("Alice", s.getName());
        assertEquals("alice@uca.edu", s.getEmail());
    }

    @Test
    void invalidBannerIdShouldThrow() {
        assertThrows(ValidationException.class, () -> new Student("123", "Bob", "bob@uca.edu"));
    }

    @Test
    void invalidEmailShouldThrow() {
        assertThrows(ValidationException.class, () -> new Student("B999", "Eve", "bademail"));
    }
}
