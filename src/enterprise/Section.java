package enterprise;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "SECTIONS")
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"semesterID", "courseID", "sectionNumber"})
)
public class Section
{
    //Table attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int sectionID;

    //A Section can be thought of as an instance of a Course.
    //A byte can contain up to 127 "instances" of a Course, which is sufficient enough.
    @Column(nullable = false)
    private byte sectionNumber;

    //Some larger lectures exceed the 127 limit of a byte.
    //The next smallest thing is a short value.
    @Column(nullable = false)
    private short maxCapacity;


    //Associations
    //Section -> Course - unidirectional
    @ManyToOne
    @JoinColumn(name = "courseID")
    private Course course;

    //Section -> Timeslot - unidirectional
    @ManyToOne
    @JoinColumn(name = "timeslotID")
    private TimeSlot timeslot;

    //Semester - Section - bidirectional
    @ManyToOne
    @JoinColumn(name = "semesterID")
    private Semester semester;

    //Section - Student (Enrollment) - bidirectional
    @ManyToMany
    @JoinTable (
            name = "ENROLLMENTS",
            joinColumns = @JoinColumn(name = "sectionID"),
            inverseJoinColumns = @JoinColumn(name = "studentSurrogateID")
    )
    private Set<Student> enrolledStudents;


    //Object methods
    public Section() {}

    public Section(byte sectionNumber, short maxCapacity, Course course, Semester semester, TimeSlot timeslot) {
        this.sectionNumber = sectionNumber;
        this.maxCapacity = maxCapacity;
        this.enrolledStudents = new HashSet<>();
        this.course = course;
        this.addSemester(semester);
        this.timeslot = timeslot;
    }

    public int getSectionID() { return sectionID; }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(byte sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(short maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Course getCourse() { return course; }

    public void setCourse(Course course) { this.course = course; }

    public TimeSlot getTimeslot() { return timeslot; }

    public void setTimeslot(TimeSlot timeslot) { this.timeslot = timeslot; }

    public Semester getSemester() { return semester; }

    public void setSemester(Semester semester) { this.semester = semester; }

    public void addSemester(Semester s) { s.addSection(this); }

    public Set<Student> getStudents() { return enrolledStudents; }

    public void setStudents(Set<Student> students) { this.enrolledStudents = students; }

    public void addStudent(Student s) { s.addSection(this); }
}
