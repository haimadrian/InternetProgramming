package driver;

import DO.graph.IGraph;
import DO.graph.MatrixAsGraph;
import DO.mat.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public class MatricesMain {
    public static void main(String[] args) throws Exception {
        Index oneone = new Index(1, 1);
        int matDimension = 5;

        AbstractBinaryMatrix mat = new StandardMatrix(matDimension, matDimension, true);
        System.out.println("Standard Matrix with neighbors of: " + oneone);
        System.out.println(mat.printMatrix());
        System.out.println(mat.neighbors(oneone));

        AbstractBinaryMatrix mat2 = new CrossMatrix(matDimension, matDimension, true);
        System.out.println(System.lineSeparator() + "Cross Matrix with neighbors of: " + oneone);
        System.out.println(mat2.printMatrix());
        System.out.println(mat2.neighbors(oneone));

        Index to = new Index(3, 3);
        int numOfTests = 30;
        testGraph("Standard matrix as graph:", matDimension, numOfTests, oneone, to, StandardMatrix.class);
        testGraph("Cross matrix as graph:", matDimension, numOfTests, oneone, to, CrossMatrix.class);
    }

    private static void testGraph(String title, int matDim, int numOfTests, Index from, Index to, Class<? extends AbstractBinaryMatrix> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Collection<Collection<Index>> paths = new ArrayList<>();
        Collection<Collection<Index>> maxPaths = new ArrayList<>();
        IGraph<Index> selectedGraph = null;

        for (int i = 0; i < numOfTests; i++) {
            AbstractBinaryMatrix mat = cls.getDeclaredConstructor(int.class, int.class, boolean.class).newInstance(matDim, matDim, true);
            var matrixAsGraph = new MatrixAsGraph<Integer>(mat, new Index(0, 0));
            paths = matrixAsGraph.findPaths(from, to);
            if (paths.size() > maxPaths.size()) {
                maxPaths = paths;
                selectedGraph = matrixAsGraph;
            }
        }

        System.out.println(System.lineSeparator() + title);
        if (!maxPaths.isEmpty()) {
            System.out.println(selectedGraph.printGraph());
            System.out.print("Adjacent vertices of " + from + ": ");
            System.out.println(selectedGraph.getAdjacentVertices(from));
            System.out.println("Paths from " + from + " to " + to + ": ");
            System.out.println(maxPaths.toString().replace("],", "]," + System.lineSeparator()));
        } else {
            System.out.println("No path could be found, given there were " + numOfTests + " tries.");
        }
    }
}

