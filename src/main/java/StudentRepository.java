import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class StudentRepository {

    public int add(Student student) throws SQLException {

        String findIdsSql = "SELECT id FROM students ORDER BY id";

        String insertSql = """
                INSERT INTO students (id, name, email, course, age, marks)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int nextId = 1;

                try (Statement statement = connection.createStatement();
                     ResultSet rs = statement.executeQuery(findIdsSql)) {

                    while (rs.next()) {
                        int existingId = rs.getInt("id");

                        if (existingId == nextId) {
                            nextId++;
                        } else if (existingId > nextId) {
                            break;
                        }
                    }
                }

                try (PreparedStatement ps =
                             connection.prepareStatement(insertSql)) {

                    ps.setInt(1, nextId);
                    bindStudent(ps, student, 2);
                    ps.executeUpdate();
                }

                connection.commit();
                return nextId;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Student> findAll() throws SQLException {

        String sql = """
                SELECT id, name, email, course, age, marks
                FROM students
                ORDER BY id
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
                FROM students
                WHERE id = ?
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

            ps.setString(1, "%" + name.trim() + "%");

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

            bindStudent(ps, student, 1);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {

        try (Connection connection = Database.getConnection()) {

            connection.setAutoCommit(false);

            try {
                String deleteSql = "DELETE FROM students WHERE id = ?";

                try (PreparedStatement ps =
                             connection.prepareStatement(deleteSql)) {

                    ps.setInt(1, id);

                    if (ps.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                List<Integer> remainingIds = new ArrayList<>();

                String selectSql = "SELECT id FROM students ORDER BY id";

                try (Statement statement = connection.createStatement();
                     ResultSet rs = statement.executeQuery(selectSql)) {

                    while (rs.next()) {
                        remainingIds.add(rs.getInt("id"));
                    }
                }

                String updateSql =
                        "UPDATE students SET id = ? WHERE id = ?";

                // Step 1: Move IDs to temporary negative values.
                try (PreparedStatement ps =
                             connection.prepareStatement(updateSql)) {

                    for (int oldId : remainingIds) {
                        ps.setInt(1, -oldId);
                        ps.setInt(2, oldId);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                // Step 2: Assign serial IDs starting from 1.
                try (PreparedStatement ps =
                             connection.prepareStatement(updateSql)) {

                    int newId = 1;

                    for (int oldId : remainingIds) {
                        ps.setInt(1, newId++);
                        ps.setInt(2, -oldId);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                connection.commit();
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
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

    private void bindStudent(
            PreparedStatement ps, Student student, int startIndex)
            throws SQLException {

        ps.setString(startIndex, student.getName().trim());
        ps.setString(startIndex + 1,
                student.getEmail().trim().toLowerCase(Locale.ROOT));
        ps.setString(startIndex + 2, student.getCourse().trim());
        ps.setInt(startIndex + 3, student.getAge());
        ps.setDouble(startIndex + 4, student.getMarks());
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
