import java.util.*;
import java.util.stream.Collectors;

/**
 * https://csacademy.com/contest/ieeextreme-practice-old/task/8761fb7efefcf1d890df1d8d91cae241
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public class BlackgatePenitentiary {
   public static void main(String[] args) {
      try (Scanner in = new Scanner(System.in)) {
         int crewMembersCount = in.nextInt();
         in.nextLine(); // Discard line terminator

         List<CrewMember> crewMembers = new ArrayList<>(crewMembersCount);
         for (int k = 0; k < crewMembersCount; k++) {
            // Each line represents a crew member where it contains a name (string) and height (int).
            String currLine = in.nextLine().trim();
            String[] nameAndHeight = currLine.split(" ");
            crewMembers.add(new CrewMember(nameAndHeight[0], Integer.parseInt(nameAndHeight[1])));
         }

         // First, group crew members by their height
         // Second, use a tree map so it will sort entries based on keys (order by height)
         Map<Integer, List<CrewMember>> sortedMap = new TreeMap<>(crewMembers.stream().collect(Collectors.groupingBy(CrewMember::getHeight)));

         // Now print the output.
         int lastUsedIndex = 1;
         for (List<CrewMember> currHeightCrews : sortedMap.values()) {
            if (currHeightCrews.size() > 1) {
               // Crew members should be sorted by name.
               currHeightCrews.sort(Comparator.comparing(CrewMember::getName));
            }

            StringBuilder sb = new StringBuilder(currHeightCrews.stream().map(CrewMember::getName).collect(Collectors.joining(" ")));
            sb.append(" ").append(lastUsedIndex);
            lastUsedIndex += (currHeightCrews.size() - 1);
            sb.append(" ").append(lastUsedIndex);
            System.out.println(sb.toString());
            lastUsedIndex++; // Go forward to next starting index
         }
      }
   }

   private static class CrewMember {
      String name;
      int height;

      public CrewMember(String name, int height) {
         this.name = name;
         this.height = height;
      }

      public String getName() {
         return name;
      }

      public int getHeight() {
         return height;
      }
   }
}
