package edu.uca.registration.app;

import java.util.Scanner;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.util.EnrollmentException;
import edu.uca.registration.util.ValidationException;

public class Main {

    public static void main(String[] args) {
        RegistrationService service;
        try {
            service = new RegistrationService("."); // use current dir for CSV files
        } catch (Exception e) {
            System.err.println("Failed to initialize application: " + e.getMessage());
            return;
        }

        boolean demo = args.length > 0 && "--demo".equalsIgnoreCase(args[0]);
        if (demo) {
            service.seedDemoData();
            System.out.println("Demo data seeded.");
        }

        System.out.println("=== UCA Course Registration ===");
        menuLoop(service);
        System.out.println("Goodbye!");
    }

    private static void menuLoop(RegistrationService service) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1) Add student");
            System.out.println("2) Add course");
            System.out.println("3) Enroll student in course");
            System.out.println("4) Drop student from course");
            System.out.println("5) List students");
            System.out.println("6) List courses");
            System.out.println("0) Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1": addStudentUI(sc, service); break;
                    case "2": addCourseUI(sc, service); break;
                    case "3": enrollUI(sc, service); break;
                    case "4": dropUI(sc, service); break;
                    case "5": listStudents(service); break;
                    case "6": listCourses(service); break;
                    case "0": return;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (ValidationException | EnrollmentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private static void addStudentUI(Scanner sc, RegistrationService service) {
        System.out.print("Banner ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        try {
            service.addStudent(id, name, email);
            System.out.println("Student added successfully.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addCourseUI(Scanner sc, RegistrationService service) {
        System.out.print("Course Code: ");
        String code = sc.nextLine().trim();
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Capacity: ");
        int capacity = Integer.parseInt(sc.nextLine().trim());
        try {
            service.addCourse(code, title, capacity);
            System.out.println("Course added successfully.");
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void enrollUI(Scanner sc, RegistrationService service) {
        System.out.print("Student ID: ");
        String sid = sc.nextLine().trim();
        System.out.print("Course Code: ");
        String code = sc.nextLine().trim();
        try {
            service.enroll(sid, code);
            System.out.println("Enrollment processed.");
        } catch (EnrollmentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void dropUI(Scanner sc, RegistrationService service) {
        System.out.print("Student ID: ");
        String sid = sc.nextLine().trim();
        System.out.print("Course Code: ");
        String code = sc.nextLine().trim();
        try {
            service.drop(sid, code);
            System.out.println("Drop processed.");
        } catch (EnrollmentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listStudents(RegistrationService service) {
        System.out.println("Students:");
        for (Student s : service.allStudents()) {
            System.out.println(" - " + s);
        }
    }

    private static void listCourses(RegistrationService service) {
        System.out.println("Courses:");
        for (Course c : service.allCourses()) {
            System.out.println(" - " + c);
        }
    }
}