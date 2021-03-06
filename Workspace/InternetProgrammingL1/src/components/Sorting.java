package components;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author Haim Adrian
 * @since 18-Feb-21
 */
public class Sorting {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        int[] ints = random.ints(10, 0, 5000).toArray();

        System.out.println("Array before: " + Arrays.toString(ints));
        Arrays.sort(ints);
        System.out.println("Array after: " + Arrays.toString(ints));
    }
}

