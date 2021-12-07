package enterprise;

import jakarta.persistence.*;

@Entity(name = "PREREQUISITES")
public class Prerequisite
{
    //Table attributes
    @Id
    @Column()
    private char minimumGrade;


    //Associations
    //Course that requires passing grade.
    @Id
    @ManyToOne
    @JoinColumn
    private Course required;

    //Course that can be taken after passing required Course.
    @Id
    @ManyToOne
    @JoinColumn
    private Course requiredFor;


    //Object methods
    public Prerequisite() {}

    public Prerequisite(char minimumGrade, Course required, Course requiredFor) {
        this.minimumGrade = minimumGrade;
        this.required = required;
        this.requiredFor = requiredFor;
        requiredFor.getPrerequisites().add(this);
    }

    public char getMinimumGrade() {
        return minimumGrade;
    }

    public Course getRequired() { return required; }

    public Course getRequiredFor() { return requiredFor; }

}
