package edu.uca.registration.model;

import java.util.ArrayList;
import java.util.List;

import edu.uca.registration.util.ValidationException;

public class Course {
    private final String code;
    private final String title;
    private final int capacity;
    private final List<String> roster = new ArrayList<>();
    private final List<String> waitlist = new ArrayList<>();

    public Course(String code, String title, int capacity) {
        validate(code, title, capacity);
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    private void validate(String code, String title, int capacity) {
        if (code == null || code.isBlank()) {
            throw new ValidationException("Course code cannot be empty.");
        }
        if (title == null || title.isBlank()) {
            throw new ValidationException("Course title cannot be empty.");
        }
        if (capacity < 1 || capacity > 500) {
            throw new ValidationException("Capacity must be between 1 and 500.");
        }
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<String> getRoster() {
        return roster;
    }

    public List<String> getWaitlist() {
        return waitlist;
    }

    @Override
    public String toString() {
        return code + " " + title + " (Cap: " + capacity + ", Enrolled: " + roster.size()
                + ", Waitlist: " + waitlist.size() + ")";
    }
}
