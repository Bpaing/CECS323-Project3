package enterprise;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "DEPARTMENTS")
public class Department
{
    //Table attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int departmentID;

    @Column(length = 128, nullable = false)
    private String name;

    @Column(length = 8, nullable = false)
    private String abbreviation;


    //Associations
    //Course - Department - bidirectional
    @OneToMany(mappedBy = "department")
    private List<Course> courses;


    //Object methods
    public Department() {}

    public Department(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.courses = new ArrayList<>();
    }

    public int getDepartmentID() { return departmentID; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<Course> getCourses() { return courses; }

    public void setCourses(List<Course> courses) { this.courses = courses; }

    public void addCourse(Course c) {
        this.courses.add(c);
        c.setDepartment(this);
    }
}
