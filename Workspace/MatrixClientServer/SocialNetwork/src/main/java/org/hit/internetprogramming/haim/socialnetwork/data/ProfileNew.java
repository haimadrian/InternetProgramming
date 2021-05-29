package org.hit.internetprogramming.haim.socialnetwork.data;

import java.util.Objects;
import java.util.Random;

public class ProfileNew {
    // thread visibility - always the most updated value

    static Random generator;
    private String name;
    private double age;
    private final Integer id;

    public ProfileNew(Integer id, String name, double age) {
        if (name != null) this.name = name;
        if (age >= 0 && age <= 130) this.age = age;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public double getAge() {
        return age;
    }

    public Integer getId() {
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
        ProfileNew objAsProfile = (ProfileNew) obj;
        return this.name.equals(objAsProfile.name) && this.age == objAsProfile.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
