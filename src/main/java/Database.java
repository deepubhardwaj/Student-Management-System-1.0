
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String URL =
            "jdbc:sqlite:student_management.db";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA busy_timeout = 5000");
        }

        return connection;
    }

    public static void initialize() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                        CHECK(length(trim(name)) > 0),
                    email TEXT NOT NULL COLLATE NOCASE UNIQUE,
                    course TEXT NOT NULL
                        CHECK(length(trim(course)) > 0),
                    age INTEGER NOT NULL
                        CHECK(age BETWEEN 16 AND 100),
                    marks REAL NOT NULL
                        CHECK(marks BETWEEN 0 AND 100),
                    created_at TEXT NOT NULL
                        DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
        }
    }
}
