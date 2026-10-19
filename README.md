# Student Management System

A console-based Student Management System developed using Java, JDBC, SQLite, and Maven. This project helps manage student records through database operations.

## Features

- Add new student records
- View student records
- Update existing student details
- Delete student records
- Store student information in an SQLite database
- Use prepared statements for database operations
- Organize the project using separate Java classes

## Technologies Used

- **Java 21** — Application development
- **JDBC** — Database connectivity
- **SQLite** — Database storage
- **Maven** — Dependency and build management

## Student Information

The system manages the following details:

- Student ID
- Name
- Email
- Course
- Age
- Marks

## Project Structure

```text
Student-Management-System-1.0/
├── src/
│   └── main/
│       └── java/
│           ├── Main.java
│           ├── Student.java
│           ├── Database.java
│           └── StudentRepository.java
├── docs/
│   ├── Project-Report.md
│   └── Project-Report.pdf
├── .gitignore
├── pom.xml
└── README.md
```

## Prerequisites

- Java JDK 21
- Apache Maven
- Git (optional, for cloning the repository)

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/deepubhardwaj/Student-Management-System-1.0.git
```

### 2. Open the project directory

```bash
cd Student-Management-System-1.0
```

### 3. Compile the project

```bash
mvn clean compile
```

### 4. Run the application

```bash
mvn exec:java
```

## Database

The project uses SQLite to store student records. The database initialization and connection logic are handled by `Database.java`, while database operations are managed by `StudentRepository.java`.

## Project Report

The detailed project report is available in the `docs/` directory:

- [Project Report (PDF)](docs/Project-Report.pdf)
- [Project Report (Markdown)](docs/Project-Report.md)

## Author

**Deepu Bhardwaj**

GitHub: [@deepubhardwaj](https://github.com/deepubhardwaj)

---

This project was developed for learning and practicing Java programming, JDBC, SQL database operations, and Maven-based project management.
