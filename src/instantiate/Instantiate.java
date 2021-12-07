package instantiate;

import enterprise.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class Instantiate {

    private static boolean alreadyInstantiated = false;

    public static void instantiateModel(EntityManager em) {
        if (alreadyInstantiated) {
            System.out.println("\nThis database has already been instantiated. Skipping...\n");
        } else {
            em.getTransaction().begin();
            Semester[] semesters = createSemesters();
            Department[] departments = createDepartments();
            Course[] courses = createCourses(departments);
            TimeSlot[] timeslots = createTimeSlots();
            Section[] sections = createSections(courses, semesters, timeslots);
            Student[] students = createStudents();
            createEnrollments(students, sections);
            for (Semester s : semesters)
                em.persist(s);
            for (Department d : departments)
                em.persist(d);
            for (Course c : courses)
                em.persist(c);
            for (TimeSlot t : timeslots)
                em.persist(t);
            for (Section s : sections)
                em.persist(s);
            for (Student s : students)
                em.persist(s);
            em.getTransaction().commit();

            //Prerequisite and Transcript require separate commits due to PK shenanigans
            em.getTransaction().begin();
            Prerequisite[] prerequisites = createPrerequisites(courses);
            for (Prerequisite pre : prerequisites)
                em.persist(pre);
            em.getTransaction().commit();

            em.getTransaction().begin();
            Transcript[] transcripts = createTranscripts(students, sections);
            for (Transcript t : transcripts)
                em.persist(t);
            em.getTransaction().commit();

            System.out.println("\nDatabase instantiated.\n");
            alreadyInstantiated = true;
        }
    }


    //Helper methods that initialize the default values for each class.
    //Because the number of objects are predefined, arrays are preferred to lists.

    private static Semester[] createSemesters() {
        Semester[] sem = new Semester[3];
        sem[0] = new Semester("Spring 2021", LocalDate.of(2021, 1, 19));
        sem[1] = new Semester("Fall 2021", LocalDate.of(2021, 8, 17));
        sem[2] = new Semester("Spring 2022", LocalDate.of(2022, 1, 20));
        return sem;
    }

    private static Department[] createDepartments() {
        Department[] d = new Department[2];
        d[0] = new Department("Computer Engineering and Computer Science", "CECS");
        d[1] = new Department("Italian Studies", "ITAL");
        return d;
    }

    private static Course[] createCourses(Department[] d) {
        Course[] c = new Course[6];
        c[0] = new Course("174", "Introduction to Programming and Problem Solving", (byte) 3, d[0]);
        c[1] = new Course("274", "Data Structures", (byte) 3, d[0]);
        c[2] = new Course("277", "Object Oriented Application Programming", (byte) 3, d[0]);
        c[3] = new Course("282", "Advanced C++", (byte) 3, d[0]);
        c[4] = new Course("101A", "Fundamentals of Italian", (byte) 4, d[1]);
        c[5] = new Course("101B", "Fundamentals of Italian", (byte) 4, d[1]);
        return c;
    }

    private static Prerequisite[] createPrerequisites(Course[] c) {
        Prerequisite[] p = new Prerequisite[5];

        //CECS174 and ITAL101A have no prerequisites.

        //CECS274 has prerequisites: 174
        Prerequisite p1 = new Prerequisite('C', c[0], c[1]);

        //CECS277 has prerequisites: 174
        Prerequisite p2 = new Prerequisite('C', c[0], c[2]);

        //CECS282 has prerequisites: 274, 277
        Prerequisite p3 = new Prerequisite('C', c[1], c[3]);
        Prerequisite p4 = new Prerequisite('C', c[2], c[3]);

        //ITAL101B has prerequisites: 101A
        Prerequisite p5 = new Prerequisite('D', c[4], c[5]);

        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        p[3] = p4;
        p[4] = p5;
        return p;
    }

    private static TimeSlot[] createTimeSlots() {
        TimeSlot[] t = new TimeSlot[4];
        //MW, 12:30 - 1:45PM
        t[0] = new TimeSlot((byte) 0b0101000, LocalTime.of(12, 30), LocalTime.of(13, 45));

        //TuTh, 2:00 - 3:15PM
        t[1] = new TimeSlot((byte) 0b0010100, LocalTime.of(14, 00), LocalTime.of(15, 15));

        //MWF, 12:00 - 12:50PM
        t[2] = new TimeSlot((byte) 0b0101010, LocalTime.of(12, 00), LocalTime.of(12, 50));

        //F, 8:00 - 10:45AM
        t[3] = new TimeSlot((byte) 0b0000010, LocalTime.of(8, 00), LocalTime.of(10, 45));
        return t;
    }

    private static Section[] createSections(Course[] c, Semester[] sem, TimeSlot[] t) {
        Section[] sec = new Section[7];
        sec[0] = new Section((byte) 1, (short) 105, c[0], sem[0], t[0]);    //a
        sec[1] = new Section((byte) 1, (short) 140, c[1], sem[1], t[1]);    //b
        sec[2] = new Section((byte) 3, (short) 35, c[2], sem[1], t[3]);     //c
        sec[3] = new Section((byte) 5, (short) 35, c[3], sem[2], t[1]);     //d
        sec[4] = new Section((byte) 1, (short) 35, c[2], sem[2], t[0]);     //e
        sec[5] = new Section((byte) 7, (short) 35, c[3], sem[2], t[0]);     //f
        sec[6] = new Section((byte) 1, (short) 25, c[4], sem[2], t[2]);     //g
        return sec;
    }

    private static Student[] createStudents() {
        Student[] stu = new Student[3];
        stu[0] = new Student(123456789, "Naomi Nagata");
        stu[1] = new Student(987654321, "James Holden");
        stu[2] = new Student(555555555, "Amos Burton");
        return stu;
    }

    private static void createEnrollments(Student[] stu, Section[] sec) {
        stu[0].addSection(sec[3]);
    }

    private static Transcript[] createTranscripts(Student[] stu, Section[] sec) {
        Transcript[] t = new Transcript[9];

        //Naomi Nagata
        Transcript t1 = new Transcript("A", stu[0], sec[0]);    //A in section(a)
        Transcript t2 = new Transcript("A", stu[0], sec[1]);    //A in section(b)
        Transcript t3 = new Transcript("A", stu[0], sec[2]);    //A in section(c)
        stu[0].getGrades().add(t1);
        stu[0].getGrades().add(t2);
        stu[0].getGrades().add(t3);

        //James Holden
        Transcript t4 = new Transcript("C", stu[1], sec[0]);    //C in section (a)
        Transcript t5 = new Transcript("C", stu[1], sec[1]);    //C in section (b)
        Transcript t6 = new Transcript("C", stu[1], sec[2]);    //C in section (c)
        stu[1].getGrades().add(t4);
        stu[1].getGrades().add(t5);
        stu[1].getGrades().add(t6);

        //Amos Burton
        Transcript t7 = new Transcript("C", stu[2], sec[0]);    //C in section (a)
        Transcript t8 = new Transcript("B", stu[2], sec[1]);    //B in section (b)
        Transcript t9 = new Transcript("D", stu[2], sec[2]);    //D in section (c)
        stu[2].getGrades().add(t7);
        stu[2].getGrades().add(t8);
        stu[2].getGrades().add(t9);

        t[0] = t1;
        t[1] = t2;
        t[2] = t3;
        t[3] = t4;
        t[4] = t5;
        t[5] = t6;
        t[6] = t7;
        t[7] = t8;
        t[8] = t9;
        return t;
    }
}
