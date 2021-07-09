package org.hit.internetprogramming.eoh.common.mat;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;

@Log4j2
public enum MatrixType {
    STANDARD(StandardMatrix.class), CROSS(CrossMatrix.class), REGULAR(RegularMatrix.class);

    final Class<?> matrixImplClass;

    MatrixType(Class<?> matrixImplClass) {
        this.matrixImplClass = matrixImplClass;
    }

    /**
     * Create a new instance of the underlying matrix of this kind.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     * @param <T> Type of the elements in a matrix
     * @return An instance of that matrix
     */
    @SuppressWarnings("unchecked")
    public <T> IMatrix<T> newInstance(int rows, int cols) {
        IMatrix<T> matrix;

        try {
            Constructor<?> ctor = matrixImplClass.getDeclaredConstructor(int.class, int.class);
            matrix = (IMatrix<T>) ctor.newInstance(rows, cols);
        } catch (Exception e) {
            // Use standard matrix by default, in case something unexpected happens
            matrix = (IMatrix<T>) new StandardMatrix(rows, cols);
            log.error(e.getMessage(), e);
        }

        return matrix;
    }
}
