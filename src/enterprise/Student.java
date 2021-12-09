package enterprise;

import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

@Entity(name = "STUDENTS")
public class Student
{
    public enum RegistrationResult { SUCCESS, ALREADY_PASSED, ENROLLED_IN_SECTION, NO_PREREQUISITES,
        ENROLLED_IN_ANOTHER, TIME_CONFLICT };

    //Table attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int studentSurrogateID;

    //CSULB Student IDs consist of 9 digits, each digit value ranging from 0 to 9.
    //The range of an integer goes up to 10 digits.
    @Column(unique = true, nullable = false)
    private int studentID;

    @Column(length = 128, nullable = false)
    private String name;

    //Associations
    //Student -> Section (Transcript) - unidirectional
    @OneToMany(mappedBy = "student")
    private Set<Transcript> grades;

    //Section - Student (Enrollment) - bidirectional
    @ManyToMany(mappedBy = "enrolledStudents")
    private Set<Section> enrolledSections;

    //Object methods
    public Student() {}

    public Student(int studentID, String name) {
        this.studentID = studentID;
        this.name = name;
        this.grades = new HashSet<>();
        this.enrolledSections = new HashSet<>();
    }

    public int getStudentSurrogateID() { return studentSurrogateID; }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Transcript> getGrades() { return grades; }

    public void setGrades(Set<Transcript> grades) { this.grades = grades; }

    public Set<Section> getEnrolledSections() { return enrolledSections; }

    public void setEnrolledSections(Set<Section> enrolledSections) { this.enrolledSections = enrolledSections; }

    public void addSection(Section s) {
        this.enrolledSections.add(s);
        s.getStudents().add(this);
    }

    public double getGpa() {
        double pointSum = 0;
        int unitSum = 0;
        for(Transcript t : grades) {
            byte units = t.getSection().getCourse().getUnits();
            unitSum += units;
            switch(t.getGradeEarned().charAt(0)) {
                case 'A':
                    pointSum += 4 * units;
                    break;
                case 'B':
                    pointSum += 3 * units;
                    break;
                case 'C':
                    pointSum += 2 * units;
                    break;
                case 'D':
                    pointSum += 1 * units;
                    break;
            }
        }
        return pointSum / unitSum;
    }

    public RegistrationResult registerForSection(Section attemptedSection) {
        Course attemptedCourse = attemptedSection.getCourse();
        List<Prerequisite> prerequisitesMet = new ArrayList<>(attemptedCourse.getPrerequisites());

        //Iterate through enrollments
        for (Section otherSection : enrolledSections) {
            //Courses are the same
            if (attemptedCourse.equals(otherSection.getCourse())) {
                //Check section numbers
                if (attemptedSection.getSectionNumber() == otherSection.getSectionNumber()) {
                    System.out.println("Failed: The student is already enrolled in the section.\n");
                    return RegistrationResult.ENROLLED_IN_SECTION;
                }
                System.out.println("Failed: The student is enrolled in a different section of that course.\n");
                return RegistrationResult.ENROLLED_IN_ANOTHER;
            }

            //Check if sections share same days - bitwise AND
            //If they share any days, then the result will be nonzero
            if ((attemptedSection.getTimeslot().getDaysOfWeek() & otherSection.getTimeslot().getDaysOfWeek()) != 0) {
                //Check for time overlap
                TimeSlot a = attemptedSection.getTimeslot();
                TimeSlot b = otherSection.getTimeslot();

                //(Time overlap) || (Start/End times are equal)
                if (!(a.getStartTime().isBefore(b.getEndTime()) && a.getEndTime().isAfter(b.getStartTime())) ||
                        (a.getStartTime().equals(b.getStartTime()) || (a.getEndTime().equals(b.getEndTime())))) {
                    System.out.println("Failed: The student is enrolled in another course section with a time " +
                            "conflict.\n");
                    return RegistrationResult.TIME_CONFLICT;
                }
            }
        }

        //Iterate through transcripts
        for (Transcript t : this.grades) {
            Course otherCourse = t.getSection().getCourse();
            char letterGrade = t.getGradeEarned().charAt(0);

            //Courses are the same, passing grade
            if (attemptedCourse.equals(otherCourse) && letterGrade <= 'C') {
                System.out.println("Failed: The student has already received a \"C\" or better in the course.\n");
                return RegistrationResult.ALREADY_PASSED;
            }

            //Check if course is part of prerequisites + passing grade
            //If all prerequisites are met, the list will be empty after the for loop.
            if (!prerequisitesMet.isEmpty()) {
                for (int i = 0; i < prerequisitesMet.size(); i++) {
                    Prerequisite p = prerequisitesMet.get(i);
                    if (p.getRequiredCourse().equals(otherCourse) && letterGrade <= p.getMinimumGrade()) {
                        prerequisitesMet.remove(p);
                    }
                }
            }
        }

        //Prerequisites not fulfilled after checking all transcript grades
        if (!prerequisitesMet.isEmpty()) {
            System.out.println("Failed: The student has not met the course prerequisites.\n");
            return RegistrationResult.NO_PREREQUISITES;
        }


        //No other results have been returned, mutate sets. Success!
        addSection(attemptedSection);
        System.out.println("Successfully registered section.\n");
        return RegistrationResult.SUCCESS;
    }
}
