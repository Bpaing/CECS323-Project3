package enterprise;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "COURSES")
@Table(uniqueConstraints =
    @UniqueConstraint(columnNames = {"departmentID", "number"})
)
public class Course
{
    //Table attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int courseID;

    @Column(length = 8, nullable = false)
    private String number;

    @Column(length = 64, nullable = false)
    private String title;

    //Most courses are either 3 units or 4 units.
    //From what I checked, CS degree requires 126 units.
    @Column(nullable = false)
    private byte units;


    //Associations
    //Course -> Course (Prerequisite) - unidirectional
    @OneToMany(mappedBy = "required")
    private Set<Prerequisite> prerequisites;

    //Course - Department - bidirectional
    @ManyToOne
    @JoinColumn(name = "departmentID")
    private Department department;


    //Object methods
    public Course() {}

    public Course(String number, String title, byte units, Department department) {
        this.number = number;
        this.title = title;
        this.units = units;
        this.addDepartment(department);
        this.prerequisites = new HashSet<>();
    }

    public int getCourseID() { return courseID; }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte getUnits() { return units; }

    public void setUnits(byte units) { this.units = units; }

    public Set<Prerequisite> getPrerequisites() { return prerequisites; }

    public void setPrerequisites(Set<Prerequisite> prerequisites) { this.prerequisites = prerequisites; }

    public Department getDepartment() { return department; }

    public void setDepartment(Department department) { this.department = department; }

    public void addDepartment(Department d) { d.addCourse(this); }
}
