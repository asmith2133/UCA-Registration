package edu.uca.registration.model;

import edu.uca.registration.util.ValidationException;

public class Student {
    private final String id;
    private final String name;
    private final String email;

    public Student(String id, String name, String email) {
        validate(id, name, email);
        this.id = id;
        this.name = name;
        this.email = email;
    }

    private void validate(String id, String name, String email) {
        if (id == null || !id.matches("^B\\d{3,}$")) {
            throw new ValidationException("Invalid Banner ID: " + id);
        }
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return id + " " + name + " <" + email + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Student other)) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
