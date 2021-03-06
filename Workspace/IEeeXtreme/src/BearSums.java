import java.util.*;
import java.util.stream.Collectors;

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
                int[] sumAndListSize = Arrays.stream(currLine.split(" ")).mapToInt(Integer::parseInt).toArray();
                Integer sum = sumAndListSize[0];

                // Now read the elements in list. We maintain a list so we can find the first match according to input order.
                currLine = in.nextLine().trim();
                int[] pair = null;
                if (!currLine.isEmpty()) {
                    List<Integer> elements = Arrays.stream(currLine.split(" ")).map(Integer::parseInt).collect(Collectors.toList());

                    // Make a set out of all elements, so we can find a match in O(1)
                    //Map<Integer, Long> elementCounts = elements.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    Map<Integer, List<Integer>> elementIndices = mapElementsToTheirIndices(elements);

                    // Keep all pairs, so we will select the first complete pair.
                    // Each element in the list is a matrix of 2X2, where first row is the pair, and second row is the index of that pair in the input.
                    List<int[][]> pairs = findAllPairsWithSpecificSum(sum, elements, elementIndices);

                    pair = findFirstCompletePair(pairs);
                }

                if (pair == null) {
                    System.out.println("!OK");
                } else {
                    System.out.println(Integer.min(pair[0], pair[1]) + " " + Integer.max(pair[0], pair[1]));
                }
            }
        }
    }

    /**
     * This function traverse the list of detected pairs and finds the first complete pair, based on each pair's indices.
     * @param pairs The list of pairs to traverse
     * @return The first complete pair, or null in case input list is empty
     */
    private static int[] findFirstCompletePair(List<int[][]> pairs) {
        int[] pair = null;
        if (!pairs.isEmpty()) {
            int[][] currPair = pairs.get(0);
            pair = new int[]{currPair[0][0], currPair[0][1]};
            int maxIndex = Integer.max(currPair[1][0], currPair[1][1]), minIndex = Integer.min(currPair[1][0], currPair[1][1]);

            // Find first complete pair
            for (int i = 1; i < pairs.size(); i++) {
                currPair = pairs.get(i);
                int currMaxIndex = Integer.max(currPair[1][0], currPair[1][1]);
                int currMinIndex = Integer.min(currPair[1][0], currPair[1][1]);

                if ((currMaxIndex < maxIndex) || (currMinIndex <= minIndex && currMaxIndex == maxIndex)) {
                    minIndex = currMinIndex;
                    maxIndex = currMaxIndex;
                    pair[0] = currPair[0][0];
                    pair[1] = currPair[0][1];
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
    private static List<int[][]> findAllPairsWithSpecificSum(Integer sum, List<Integer> elements, Map<Integer, List<Integer>> elementIndices) {
        List<int[][]> pairs = new ArrayList<>();

        for (Integer currElement : elements) {
            int matchingElement = sum - currElement;
            if (elementIndices.containsKey(matchingElement)) {
                // When sum is 2*currElement, avoid of thinking there is a match with ourselves. Make sure there are two occurrences at least.
                List<Integer> indices = elementIndices.get(matchingElement);
                if (matchingElement == currElement) {
                    if (indices.size() != 1) {
                        int x = currElement;
                        int y = currElement;
                        int indexOfX = indices.get(0);
                        int indexOfY = indices.get(1);
                        pairs.add(new int[][] {{x, y}, {indexOfX, indexOfY}});
                    }
                } else {
                    int x = Integer.min(currElement, matchingElement);
                    int y = Integer.max(currElement, matchingElement);
                    int indexOfX = elementIndices.get(x).get(0);
                    int indexOfY = elementIndices.get(y).get(0);
                    pairs.add(new int[][] {{x, y}, {indexOfX, indexOfY}});
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
        Map<Integer, List<Integer>> elementIndices = new HashMap<>(elements.size());
        for (int index = 0; index < elements.size(); index++) {
            Integer currElement = elements.get(index);
            Integer finalIndex = index;
            elementIndices.compute(currElement, (key, existingValue) -> {
                if (existingValue == null) {
                    List<Integer> value = new ArrayList<>();
                    value.add(finalIndex);
                    return value;
                } else {
                    existingValue.add(finalIndex);
                    return existingValue;
                }
            });
        }
        return elementIndices;
    }
}

