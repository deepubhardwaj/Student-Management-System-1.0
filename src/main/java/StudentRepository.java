
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class StudentRepository {

    public int add(Student student) throws SQLException {

        String sql = """
                INSERT INTO students(name, email, course, age, marks)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            bindStudent(ps, student);
            ps.executeUpdate();

            try (Statement statement = connection.createStatement();
                 ResultSet rs =
                         statement.executeQuery("SELECT last_insert_rowid()")) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Could not retrieve the new student ID.");
    }

    public List<Student> findAll() throws SQLException {

        String sql = """
                SELECT id, name, email, course, age, marks
                FROM students ORDER BY id
                """;

        List<Student> students = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(map(rs));
            }
        }

        return students;
    }

    public Student findById(int id) throws SQLException {

        String sql = """
                SELECT id, name, email, course, age, marks
                FROM students WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Student> searchByName(String name) throws SQLException {

        String sql = """
                SELECT id, name, email, course, age, marks
                FROM students
                WHERE name LIKE ? COLLATE NOCASE
                ORDER BY name
                """;

        List<Student> students = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(map(rs));
                }
            }
        }

        return students;
    }

    public boolean update(int id, Student student) throws SQLException {

        String sql = """
                UPDATE students
                SET name = ?, email = ?, course = ?, age = ?, marks = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            bindStudent(ps, student);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countStudents() throws SQLException {

        String sql = "SELECT COUNT(*) FROM students";

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public double averageMarks() throws SQLException {

        String sql = "SELECT AVG(marks) FROM students";

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }

    public Optional<Student> topPerformer() throws SQLException {

        String sql = """
                SELECT id, name, email, course, age, marks
                FROM students
                ORDER BY marks DESC, name
                LIMIT 1
                """;

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            return rs.next()
                    ? Optional.of(map(rs))
                    : Optional.empty();
        }
    }

    private void bindStudent(PreparedStatement ps, Student student)
            throws SQLException {

        ps.setString(1, student.getName().trim());
        ps.setString(2,
                student.getEmail().trim().toLowerCase(Locale.ROOT));
        ps.setString(3, student.getCourse().trim());
        ps.setInt(4, student.getAge());
        ps.setDouble(5, student.getMarks());
    }

    private Student map(ResultSet rs) throws SQLException {

        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("course"),
                rs.getInt("age"),
                rs.getDouble("marks")
        );
    }
}
