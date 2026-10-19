
public class Student {

    private int id;
    private final String name;
    private final String email;
    private final String course;
    private final int age;
    private final double marks;

    public Student(String name, String email, String course,
                   int age, double marks) {
        this(0, name, email, course, age, marks);
    }

    public Student(int id, String name, String email, String course,
                   int age, double marks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.age = age;
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }

    public int getAge() {
        return age;
    }

    public double getMarks() {
        return marks;
    }

    @Override
    public String toString() {
        return "Student{id=" + id
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + ", course='" + course + '\''
                + ", age=" + age
                + ", marks=" + marks + '}';
    }
}
