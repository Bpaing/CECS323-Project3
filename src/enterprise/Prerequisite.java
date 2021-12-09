package enterprise;

import jakarta.persistence.*;

@Entity(name = "PREREQUISITES")
public class Prerequisite
{
    //Table attributes
    @Column(nullable = false)
    private char minimumGrade;


    //Associations
    //Course that requires passing grade.
    @Id
    @ManyToOne
    @JoinColumn
    private Course requiredCourse;

    @Id
    @ManyToOne
    @JoinColumn
    private Course requiredFor;


    //Object methods
    public Prerequisite() {}

    public Prerequisite(char minimumGrade, Course requiredCourse, Course requiredFor) {
        this.minimumGrade = minimumGrade;
        this.requiredCourse = requiredCourse;
        this.requiredFor = requiredFor;
        requiredFor.getPrerequisites().add(this);
    }

    public char getMinimumGrade() {
        return minimumGrade;
    }

    public void setMinimumGrade(char minimumGrade) { this.minimumGrade = minimumGrade; }

    public Course getRequiredCourse() { return requiredCourse; }

    public Course getRequiredFor() { return requiredFor; }

}
