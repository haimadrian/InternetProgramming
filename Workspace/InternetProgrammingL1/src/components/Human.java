package components;

import java.util.Objects;

/**
 * @author Haim Adrian
 * @since 18-Feb-21
 */
public class Human {
    private static int idCounter = 0;

    // Data members
    private final long id;
    private String name;
    private double age;

    // CTOR
    public Human(String name, double age) {
        this.id = ++idCounter;
        this.name = name;
        this.age = age;
    }

    // Methods that act upon the data
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!Objects.equals(this.getName(), name)) {
            this.name = name;
        } else {
            System.out.println("Received same name. Nothing to do.");
        }
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", name='" + name + "', age=" + age + '}';
    }

    public static void main(String[] args) {
        Human yossi = new Human("Yossi", 40.5);
        // 1. Memory is allocated in heap. (It has an address)
        // 2. Java initializes each data member to its default value
        // 3. Constructor is invoked
        // 4. "new" expression gets its value (components.Human@AdressInMemoryInHex)
        // 5. yossi variable is assigned with the value from phase 4.

        Human alex = new Human("Alex", 60);

        // Pay attention that when we define a local variable, it is initialized with garbage value.
        // We must set it some value before we can use it. Unlike default initializers in an object, that java sets default values to data members.
        int num; // Garbage
        int num2 = 0; // Good.
    }
}

