package edu.uca.registration.model;

import edu.uca.registration.util.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void validCourseShouldInitializeCorrectly() {
        Course c = new Course("CSCI4490", "Software Engineering", 2);
        assertEquals("CSCI4490", c.getCode());
        assertEquals(2, c.getCapacity());
        assertTrue(c.getRoster().isEmpty());
        assertTrue(c.getWaitlist().isEmpty());
    }

    @Test
    void invalidCapacityShouldThrow() {
        assertThrows(ValidationException.class, () -> new Course("CSCI", "NO", 0));
    }

    @Test
    void invalidTitleShouldThrow() {
        assertThrows(ValidationException.class, () -> new Course("CSCI", "", 10));
    }
}
