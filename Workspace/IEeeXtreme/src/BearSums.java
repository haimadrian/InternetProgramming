import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://csacademy.com/ieeextreme-practice/task/bear-sums/statement/
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public class BearSums {
    public static void main (String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int testCases = in.nextInt();
            in.nextLine(); // Discard line terminator

            for (int k = 0; k < testCases; k++) {
                // First line of each test case is S and E, where S is the sum, and E is the amount of elements in list.
                String currLine = in.nextLine().trim();
                IntPair sumAndListSize = new IntPair(Arrays.stream(currLine.split(" ")).mapToInt(Integer::parseInt).toArray());
                Integer sum = sumAndListSize.getLeft();

                // Now read the elements in list. We maintain a list so we can find the first match according to input order.
                currLine = in.nextLine().trim();
                IntPair pair = null;
                if (!currLine.isEmpty()) {
                    List<Integer> elements = Arrays.stream(currLine.split(" ")).map(Integer::parseInt).collect(Collectors.toList());

                    // Make a set out of all elements, so we can find a match in O(1)
                    Map<Integer, List<Integer>> elementIndices = mapElementsToTheirIndices(elements);

                    // Keep all pairs, so we will select the first complete pair.
                    // Each element in the list is a matrix of 2X2, where first row is the pair, and second row is the index of that pair in the input.
                    List<Pair<IntPair>> pairs = findAllPairsWithSpecificSum(sum, elements, elementIndices);

                    pair = findFirstCompletePair(pairs);
                }

                if (pair == null) {
                    System.out.println("!OK");
                } else {
                    System.out.println(pair.getLeft() + " " + pair.getRight());
                }
            }
        }
    }

    /**
     * This function traverse the list of detected pairs and finds the first complete pair, based on each pair's indices.
     * @param pairs The list of pairs to traverse
     * @return The first complete pair, or null in case input list is empty
     */
    private static IntPair findFirstCompletePair(List<Pair<IntPair>> pairs) {
        IntPair pair = null;
        if (!pairs.isEmpty()) {
            Pair<IntPair> currPair = pairs.get(0);
            pair = new IntPair(currPair.getLeft().getLeft(), currPair.getRight().getLeft());
            int maxIndex = Integer.max(currPair.getLeft().getRight(), currPair.getRight().getRight());
            int minIndex = Integer.min(currPair.getLeft().getRight(), currPair.getRight().getRight());

            // Find first complete pair
            for (int i = 1; i < pairs.size(); i++) {
                currPair = pairs.get(i);
                int currMaxIndex = Integer.max(currPair.getLeft().getRight(), currPair.getRight().getRight());
                int currMinIndex = Integer.min(currPair.getLeft().getRight(), currPair.getRight().getRight());

                // First compare by maximum index, if we have found a lower maximum, then select it.
                // Otherwise, in case both maximums equal, compare by minimum index.
                if ((currMaxIndex < maxIndex) || (currMinIndex <= minIndex && currMaxIndex == maxIndex)) {
                    minIndex = currMinIndex;
                    maxIndex = currMaxIndex;
                    pair.left = currPair.getLeft().getLeft();
                    pair.right = currPair.getRight().getLeft();
                }
            }
        }
        return pair;
    }

    /**
     * Traverse elements and fund pairs that their sum equals to the specified sum.<br/>
     * For each such pair, we add it as a matrix where first row contains the pair, and second row contains their indices.<br/>
     * The indices are kept in order to find later the first complete pair.
     * @param sum Sum to find two elements that their sum equals to
     * @param elements Elements to traverse
     * @param elementIndices See {@link #mapElementsToTheirIndices(List)}
     * @return List of pairs, or empty if there is no pair.
     */
    private static List<Pair<IntPair>> findAllPairsWithSpecificSum(Integer sum, List<Integer> elements, Map<Integer, List<Integer>> elementIndices) {
        List<Pair<IntPair>> pairs = new ArrayList<>();

        for (Integer currElement : elements) {
            int matchingElement = sum - currElement;
            if (elementIndices.containsKey(matchingElement)) {
                // When sum is 2*currElement, avoid of thinking there is a match with ourselves. Make sure there are two occurrences at least.
                List<Integer> indices = elementIndices.get(matchingElement);
                if (matchingElement == currElement) {
                    if (indices.size() != 1) {
                        pairs.add(new Pair<IntPair>(new IntPair(currElement, indices.get(0)), new IntPair(currElement, indices.get(1))));
                    }
                } else {
                    int min = Integer.min(currElement, matchingElement);
                    int max = Integer.max(currElement, matchingElement);
                    int indexOfMin = elementIndices.get(min).get(0);
                    int indexOfMax = elementIndices.get(max).get(0);
                    pairs.add(new Pair<IntPair>(new IntPair(min, indexOfMin), new IntPair(max, indexOfMax)));
                }
            }
        }
        return pairs;
    }

    /**
     * We construct map out of the elements, where element value is the key, and value is the list of indices where this element presents.<br/>
     * For example, assuming the input list is: [4 5 3 4], the map will be: {4=[0, 3], 5=[1], 3=[2]} (4 is found at indices 0 and 3.)<br/>
     * The reason we use this map is to getting the ability to find a match for a number, such that their sum is the requested sum, but do this
     * match in O(1), to keep the algorithm efficiency linear, and not polynomial.<br/>
     * We keep the indices and not only count because we must respond with the first complete pair.<br/>
     * So, in our example, if we want sum=8, we can get it as 4+4, and as 3+5, but 3 and 5 are the first complete pair, since the second 4 is last in the list.
     * @param elements List of elements to construct a map from
     * @return The map.
     */
    private static Map<Integer, List<Integer>> mapElementsToTheirIndices(List<Integer> elements) {
        //@formatter:off
        Map<Integer, List<Integer>> result =
              IntStream.range(0, elements.size()) // Generate an IntStream out of the indices [0, elements.size()-1]
                       .boxed() // Make that a Stream<Integer>
                       .collect(Collectors.groupingBy(elements::get, Collectors.toList())); // Group indices based on the value at each index
        return result;
        //@formatter:on
    }

    private static class IntPair {
        int left;
        int right;

        public IntPair(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public IntPair(int[] input) {
            left = input[0];
            right = input[1];
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }
    }

    private static class Pair<T> {
        T left;
        T right;

        public Pair(T left, T right) {
            this.left = left;
            this.right = right;
        }

        public T getLeft() {
            return left;
        }

        public T getRight() {
            return right;
        }
    }
}

