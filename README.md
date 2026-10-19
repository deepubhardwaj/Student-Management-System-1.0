
# Student Management System

A console-based Student Management System developed using Java, JDBC, Maven, and SQLite.

## Features

- Add new student records
- Display all students
- Search students by ID or name
- Update student details
- Delete student records
- View student statistics
- Calculate average marks
- Identify top-performing students
- Store records using SQLite database
- Validate student input

## Technologies Used

- Java 21
- JDBC
- SQLite
- Apache Maven
- Git and GitHub

## Project Structure

```text
Student-Management-System/
├── docs/
│   └── Project-Report.md
├── src/
│   └── main/
│       └── java/
│           └── Database.java
│           └── Main.java
│           └── Student.java
│           └── StudentRepository.java
├── .gitignore
├── README.md
└── pom.xml
```

## Prerequisites

- Java JDK 21
- Apache Maven

Verify installation:

```bash
java -version
mvn -version
```

## How to Run

Open the terminal in the project root directory.

Compile the project:

```bash
mvn clean compile
```

Run the application:

```bash
mvn exec:java
```

## Database

The application uses SQLite to store student records locally. The database is initialized by the application.

The local database file is excluded from GitHub using `.gitignore`.

## Project Report

The project report is available in the `docs` directory.

## Author

Deepu Bhardwaj
