package org.hit.internetprogramming.haim.socialnetwork.data;

import java.util.Objects;

public class Profile implements Comparable<Profile> {
    // thread visibility - always the most updated value
    private static volatile Long idCounter = 0L;

    private String name;
    private double age;
    private final Long id;

    public Profile(String name, double age) {
        if (name != null) this.name = name;
        if (age >= 0 && age <= 130) this.age = age;
        this.id = incrementAndGet();
    }

    public Profile(String name) {
        this(name, 0);
        System.out.println("Invoked from second constructor");
    }

    public static synchronized Long incrementAndGet() {
        ++idCounter;
        return idCounter;
    }

    public static Long getIdCounter() {
        return Profile.idCounter;
    }

    public static void main(String[] args) {

    }

    public String getName() {
        return this.name;
    }

    public double getAge() {
        return age;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "(Name = " + name + ", Age = " + age + ")";
    }

    /**
     * equality between all data members (id excluded)
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        // this and obj are of the same runtime class (both are Human)
        Profile objAsProfile = (Profile) obj;
        return this.name.equals(objAsProfile.name) && this.age == objAsProfile.age;
    }

    @Override
    public int compareTo(Profile o) {
        return Long.compare(this.id, o.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
