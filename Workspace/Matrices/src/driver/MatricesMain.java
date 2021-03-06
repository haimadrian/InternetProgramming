package driver;

import DO.StandardMatrix;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public class MatricesMain {
    public static void main(String[] args) {
        StandardMatrix mat = new StandardMatrix(5, 5);
        System.out.println(mat.printMatrix());
    }
}

