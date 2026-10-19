
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner INPUT = new Scanner(System.in);
    private static final StudentRepository REPOSITORY =
            new StudentRepository();

    public static void main(String[] args) {

        try (INPUT) {
            Database.initialize();

            System.out.println("\n==========================================");
            System.out.println("       STUDENT MANAGEMENT SYSTEM");
            System.out.println("==========================================");
            System.out.println("Student records are saved automatically.");

            runMenu();

        } catch (SQLException e) {
            System.err.println(
                    "Database error: " + e.getMessage()
            );
        }
    }

    private static void runMenu() {

        boolean running = true;

        while (running) {

            System.out.println("\n--------------- MAIN MENU ---------------");
            System.out.println("1. Add student");
            System.out.println("2. View all students");
            System.out.println("3. Search student");
            System.out.println("4. Update student");
            System.out.println("5. Delete student");
            System.out.println("6. View dashboard");
            System.out.println("7. Exit");

            int choice = readInt("Choose an option: ", 1, 7);

            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAllStudents();
                    case 3 -> searchStudent();
                    case 4 -> updateStudent();
                    case 5 -> deleteStudent();
                    case 6 -> showDashboard();
                    case 7 -> {
                        running = false;
                        System.out.println(
                                "Thank you for using Student Management System!"
                        );
                    }
                    default -> System.out.println("Invalid option.");
                }

            } catch (SQLException e) {
                System.out.println(
                        "Database operation failed: " + e.getMessage()
                );
            }
        }
    }

    private static void addStudent() throws SQLException {

        System.out.println("\n--- Add Student ---");

        String name = readRequired("Full name: ");
        String email = readEmail("Email: ");
        String course = readRequired("Course: ");
        int age = readInt("Age (16-100): ", 16, 100);
        double marks = readDouble("Marks (0-100): ", 0, 100);

        Student student =
                new Student(name, email, course, age, marks);

        int id = REPOSITORY.add(student);

        System.out.println(
                "Student added successfully. Assigned ID: " + id
        );
    }

    private static void viewAllStudents() throws SQLException {

        List<Student> students = REPOSITORY.findAll();

        printStudents(students);
    }

    private static void searchStudent() throws SQLException {

        System.out.println("\n--- Search Student ---");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by name");

        int option = readInt("Choose: ", 1, 2);

        if (option == 1) {

            int id = readInt(
                    "Student ID: ", 1, Integer.MAX_VALUE
            );

            Student student = REPOSITORY.findById(id);

            if (student == null) {
                System.out.println(
                        "No student found with ID " + id
                );
            } else {
                printStudents(List.of(student));
            }

        } else {

            String name = readRequired(
                    "Name (or part of name): "
            );

            printStudents(REPOSITORY.searchByName(name));
        }
    }

    private static void updateStudent() throws SQLException {

        System.out.println("\n--- Update Student ---");

        int id = readInt(
                "Student ID to update: ", 1, Integer.MAX_VALUE
        );

        Student existing = REPOSITORY.findById(id);

        if (existing == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println(
                "Press Enter to keep an existing value."
        );

        String name = readOptional(
                "Full name [" + existing.getName() + "]: ",
                existing.getName()
        );

        String email = readEmailOptional(
                "Email [" + existing.getEmail() + "]: ",
                existing.getEmail()
        );

        String course = readOptional(
                "Course [" + existing.getCourse() + "]: ",
                existing.getCourse()
        );

        int age = readOptionalInt(
                "Age [" + existing.getAge() + "]: ",
                existing.getAge(), 16, 100
        );

        double marks = readOptionalDouble(
                "Marks [" + existing.getMarks() + "]: ",
                existing.getMarks(), 0, 100
        );

        Student updated =
                new Student(name, email, course, age, marks);

        if (REPOSITORY.update(id, updated)) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("No changes were saved.");
        }
    }

    private static void deleteStudent() throws SQLException {

        System.out.println("\n--- Delete Student ---");

        int id = readInt(
                "Student ID to delete: ", 1, Integer.MAX_VALUE
        );

        Student student = REPOSITORY.findById(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        printStudents(List.of(student));

        String confirm = readRequired(
                "Type YES to permanently delete this record: "
        );

        if (confirm.equals("YES")) {

            boolean deleted = REPOSITORY.delete(id);

            if (deleted) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student could not be deleted.");
            }

        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private static void showDashboard() throws SQLException {

        System.out.println("\n------------- DASHBOARD ----------------");

        System.out.println(
                "Total students : " + REPOSITORY.countStudents()
        );

        System.out.printf(
                "Average marks  : %.2f%n",
                REPOSITORY.averageMarks()
        );

        System.out.println(
                "Top performer  : "
                        + REPOSITORY.topPerformer()
                        .map(s -> s.getName() + " (" + s.getMarks() + ")")
                        .orElse("No records yet")
        );

        System.out.println("----------------------------------------");
    }

    private static void printStudents(List<Student> students) {

        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        System.out.printf(
                "%-5s %-24s %-30s %-20s %-5s %-8s%n",
                "ID", "NAME", "EMAIL", "COURSE", "AGE", "MARKS"
        );

        System.out.println("-".repeat(100));

        for (Student student : students) {

            System.out.printf(
                    "%-5d %-24s %-30s %-20s %-5d %-8.2f%n",
                    student.getId(),
                    shorten(student.getName(), 23),
                    shorten(student.getEmail(), 29),
                    shorten(student.getCourse(), 19),
                    student.getAge(),
                    student.getMarks()
            );
        }

        System.out.println("Records shown: " + students.size());
    }

    private static String shorten(String value, int max) {

        return value.length() <= max
                ? value
                : value.substring(0, max - 3) + "...";
    }

    private static String readRequired(String prompt) {

        while (true) {

            System.out.print(prompt);
            String value = INPUT.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }

    private static String readEmail(String prompt) {

        while (true) {

            String email = readRequired(prompt);

            if (isValidEmail(email)) {
                return email;
            }

            System.out.println(
                    "Please enter a valid email address."
            );
        }
    }

    private static String readEmailOptional(
            String prompt, String current) {

        while (true) {

            String email = readOptional(prompt, current);

            if (isValidEmail(email)) {
                return email;
            }

            System.out.println(
                    "Please enter a valid email address."
            );
        }
    }

    private static boolean isValidEmail(String email) {

        return email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        );
    }

    private static int readInt(
            String prompt, int min, int max) {

        while (true) {

            System.out.print(prompt);

            try {
                int value = Integer.parseInt(
                        INPUT.nextLine().trim()
                );

                if (value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException ignored) {
                // Invalid integer input.
            }

            System.out.println(
                    "Enter a whole number between "
                            + min + " and " + max + "."
            );
        }
    }

    private static double readDouble(
            String prompt, double min, double max) {

        while (true) {

            System.out.print(prompt);

            try {
                double value = Double.parseDouble(
                        INPUT.nextLine().trim()
                );

                if (Double.isFinite(value)
                        && value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException ignored) {
                // Invalid numeric input.
            }

            System.out.println(
                    "Enter a number between "
                            + min + " and " + max + "."
            );
        }
    }

    private static String readOptional(
            String prompt, String current) {

        System.out.print(prompt);

        String value = INPUT.nextLine().trim();

        return value.isBlank() ? current : value;
    }

    private static int readOptionalInt(
            String prompt, int current, int min, int max) {

        while (true) {

            System.out.print(prompt);
            String value = INPUT.nextLine().trim();

            if (value.isBlank()) {
                return current;
            }

            try {
                int parsed = Integer.parseInt(value);

                if (parsed >= min && parsed <= max) {
                    return parsed;
                }

            } catch (NumberFormatException ignored) {
                // Invalid integer input.
            }

            System.out.println(
                    "Enter a whole number between "
                            + min + " and " + max + "."
            );
        }
    }

    private static double readOptionalDouble(
            String prompt, double current, double min, double max) {

        while (true) {

            System.out.print(prompt);
            String value = INPUT.nextLine().trim();

            if (value.isBlank()) {
                return current;
            }

            try {
                double parsed = Double.parseDouble(value);

                if (Double.isFinite(parsed)
                        && parsed >= min && parsed <= max) {
                    return parsed;
                }

            } catch (NumberFormatException ignored) {
                // Invalid numeric input.
            }

            System.out.println(
                    "Enter a number between "
                            + min + " and " + max + "."
            );
        }
    }
}
