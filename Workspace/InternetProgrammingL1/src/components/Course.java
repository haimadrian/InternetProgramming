package components;

import java.util.Arrays;

/**
 * @author Haim Adrian
 * @since 18-Feb-21
 */
public class Course {
    private static long idCounter;

    private final long privateId;
    private final String courseName;
    private final int courseNumber;
    private final int maxAmountOfStudents;
    private int amountOfStudents;
    private final Human[] students;

    public Course(String courseName, int courseNumber, int maxAmountOfStudents) {
        privateId = ++idCounter;
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.maxAmountOfStudents = Math.max(maxAmountOfStudents, 0);
        students = new Human[maxAmountOfStudents];
    }

    public Course(String courseName, int courseNumber) {
        this(courseName, courseNumber, 20);
    }

    public RegistrationStatus register(Human student) {
        RegistrationStatus status = RegistrationStatus.NONE;
        if (student != null) {
            if (amountOfStudents == students.length) {
                status = RegistrationStatus.COURSE_IS_FULL;
                System.out.println("Unable to register. Course is full.");
            } else {
                boolean exists = false;
                for (int i = 0; i < amountOfStudents && !exists; i++) {
                    exists = students[i].getId() == student.getId();
                }

                if (!exists) {
                    students[amountOfStudents++] = student;
                    status = RegistrationStatus.SUCCESS;
                    System.out.println("Registered student: " + student);
                } else {
                    status = RegistrationStatus.ALREADY_REGISTERED;
                    System.out.println("Student is already registered. [student=" + student + "]");
                }
            }
        }

        return status;
    }

    /**
     * Calculate age average in the course. If the average is an integer, return average+1. Otherwise, return the average itself.
     * @return Abe average in the course.
     */
    public double studentsAgeAverage() {
        double avg = (students.length == 0) ? 0 : Arrays.stream(students).mapToDouble(Human::getAge).average().orElse(0);
        return ((int)avg == avg) ? avg + 1 : avg;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public int getMaxAmountOfStudents() {
        return maxAmountOfStudents;
    }

    public int getAmountOfStudents() {
        return amountOfStudents;
    }

    public enum RegistrationStatus {
        COURSE_IS_FULL, SUCCESS, ALREADY_REGISTERED, NONE
    }
}

