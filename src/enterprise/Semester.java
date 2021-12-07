package enterprise;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity(name = "SEMESTERS")
public class Semester
{
    //Table attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int semesterID;

    @Column(length = 16, nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;


    //Associations
    //Semester - Section - bidirectional
    @OneToMany(mappedBy = "semester")
    private List<Section> availableSections;


    //Object methods
    public Semester() {}

    public Semester(String title, LocalDate startDate) {
        this.title = title;
        this.startDate = startDate;
        this.availableSections = new ArrayList<>();
    }

    public int getSemesterID() { return semesterID; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<Section> getAvailableSections() { return availableSections; }

    public void setAvailableSections(List<Section> availableSections) { this.availableSections = availableSections; }

    public void addSection(Section s) {
        this.availableSections.add(s);
        s.setSemester(this);
    }
}
