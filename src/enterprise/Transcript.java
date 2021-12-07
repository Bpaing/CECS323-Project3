package enterprise;

import jakarta.persistence.*;

@Entity(name = "TRANSCRIPTS")
public class Transcript implements Comparable<Transcript>
{
    //Table attributes
    @Id
    @Column(length = 2)
    private String gradeEarned;


    //Associations
    @Id
    @ManyToOne
    @JoinColumn(name = "studentSurrogateID")
    private Student student;

    @Id
    @ManyToOne
    @JoinColumn(name = "sectionID")
    private Section section;


    //Object methods
    public Transcript() {}

    public Transcript(String gradeEarned, Student student, Section section) {
        this.gradeEarned = gradeEarned;
        this.student = student;
        this.section = section;
    }

    public String getGradeEarned() {
        return gradeEarned;
    }

    public void setGradeEarned(String gradeEarned) {
        this.gradeEarned = gradeEarned;
    }

    public Student getStudent() { return student; }

    public Section getSection() { return section; }

    public String toString() {
        String course = section.getCourse().getDepartment().getAbbreviation() + " " + section.getCourse().getNumber();
        String semester = section.getSemester().getTitle();
        return course + ", " + semester + ". Grade earned: " + gradeEarned;
    }

    @Override
    public int compareTo(Transcript t){
        //Order: Semester Start Date -> Course Abbreviation -> Course Number
        if (this.getSection().getSemester().getStartDate().compareTo(t.getSection().getSemester().getStartDate()) == 0) {
            if (this.getSection().getCourse().getNumber().compareTo(t.getSection().getCourse().getNumber()) == 0)
                this.getSection().getCourse().getDepartment().getAbbreviation().compareTo(t.getSection().getCourse().getDepartment().getAbbreviation());
            return this.getSection().getCourse().getNumber().compareTo(t.getSection().getCourse().getNumber());
        }
        return this.getSection().getSemester().getStartDate().compareTo(t.getSection().getSemester().getStartDate());
    }
}
