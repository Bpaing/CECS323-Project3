import instantiate.*;
import enterprise.*;
import jakarta.persistence.*;

import java.util.*;

public class Registration {

    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("Project3");
        EntityManager em = factory.createEntityManager();

        boolean sentinel = true;
        while (sentinel) {
            System.out.print("" +
                    "(a) Instantiate database\n" +
                    "(b) Student lookup\n" +
                    "(c) Register for a course\n" +
                    "'q' to quit\n" +
                    "Input: ");
            String input = in.nextLine();
            if (input.equalsIgnoreCase("a")) {
                Instantiate.instantiateModel(em);
            } else if (input.equalsIgnoreCase("b")) {
                Student student = studentLookup(em);
                transcriptGPA(student);
            } else if (input.equalsIgnoreCase("c")) {
                registerForCourse(em);
            } else if (input.equalsIgnoreCase("q")) {
                sentinel = false;
            }
        }
        em.close();
        in.close();
    }

    private static Student studentLookup(EntityManager em){
        Student selectedStudent = null;
        while (selectedStudent == null) {
            System.out.printf("Enter the full name of a student: ");
            String name = in.nextLine();
            var query = em.createQuery("SELECT s FROM STUDENTS s WHERE UPPER(s.name) LIKE ?1", Student.class);
            query.setParameter(1, name.toUpperCase());
            try {
                selectedStudent = query.getSingleResult();
            } catch (NoResultException e) {
                System.out.printf("%s was not found in the database.\n", name);
            }
        }
        return selectedStudent;
    }

    private static void transcriptGPA(Student student) {
        System.out.println("\n" + student.getName());
        //Print out transcript in ascending order by semester start date
        Transcript[] transcript = student.getGrades().toArray(new Transcript[student.getGrades().size()]);
        Arrays.sort(transcript);
        for(Transcript t : transcript) {
            System.out.println(t);
        }
        //Print out GPA
        System.out.printf("GPA: %.3f\n\n", student.getGpa());
    }

    public static void registerForCourse(EntityManager em) {
        //Choose Semester
        Semester selectedSemester = null;
        String input;
        while (selectedSemester == null) {
            System.out.print("\nEnter a Semester by title: ");
            input = in.nextLine();

            var query = em.createQuery("SELECT s FROM SEMESTERS s WHERE UPPER(s.title) LIKE ?1", Semester.class);
            query.setParameter(1, input.toUpperCase());
            try {
                selectedSemester = query.getSingleResult();
            } catch (NoResultException e) {
                System.out.printf("%s was not found in the database.\n", input);
            }
        }

        //Choose Student
        Student selectedStudent = studentLookup(em);

        //Choose Section
        Section selectedSection = null;
        while  (selectedSection == null) {
            System.out.println("Enter the name of a course section in format: [department] [course number]-[section " +
                    "number] | Example: CECS 277-05");
            input = in.nextLine();
            String[] parse = input.split("[ -]");
            String abbreviation = parse[0];
            String courseNumber = parse[1];
            byte sectionNumber = (byte) Integer.parseInt(parse[2]);
            var query = em.createQuery("SELECT s from SECTIONS s " +
                    "JOIN s.course c JOIN c.department d " +
                    "WHERE d.abbreviation = ?1 AND c.number = ?2 AND s.sectionNumber = ?3", Section.class);
            query.setParameter(1, abbreviation.toUpperCase());
            query.setParameter(2, courseNumber);
            query.setParameter(3, sectionNumber);
            try {
                selectedSection = query.getSingleResult();
                if (!(selectedSection.getSemester().equals(selectedSemester))) {
                    System.out.printf("%s is not being offered in %s.\n", input, selectedSemester.getTitle());
                    selectedSection = null;
                }
            } catch (NoResultException e) {
                System.out.printf("%s was not found in the database.\n", input);
            }
        }

        //Registration attempt + Save to database if successful
        String formatted = String.format("%s %s-%02d", selectedSection.getCourse().getDepartment().getAbbreviation(),
                selectedSection.getCourse().getNumber(), selectedSection.getSectionNumber());
        System.out.printf("Attempting to register %s for %s.....\n", formatted, selectedStudent.getName());
        em.getTransaction().begin();
        selectedStudent.registerForSection(selectedSection);
        em.getTransaction().commit();
    }
    
}

