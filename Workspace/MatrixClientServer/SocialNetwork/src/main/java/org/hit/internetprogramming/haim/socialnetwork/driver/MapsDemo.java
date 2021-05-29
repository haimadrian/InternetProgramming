package org.hit.internetprogramming.haim.socialnetwork.driver;

import org.hit.internetprogramming.haim.socialnetwork.data.Profile;
import org.hit.internetprogramming.haim.socialnetwork.data.ProfileNew;

import java.util.HashMap;
import java.util.Map;

public class MapsDemo {
    public static void main(String[] args) {
        Profile yosef1 = new Profile("Yosef Cohen", 30);
        Profile tamar1 = new Profile("Tamar Levi", 40);
        Profile shoshi1 = new Profile("Shoshi Weiss", 60.5);

        System.out.println(yosef1.getId());
        System.out.println(tamar1.getId());
        System.out.println(shoshi1.getId());

        System.out.println(yosef1.compareTo(tamar1)); // expected: -1

        Map<Long, Profile> profileMap = new HashMap<>();
        Profile archiveYosef = profileMap.put(yosef1.getId(), yosef1);
        System.out.println(archiveYosef);
        profileMap.put(tamar1.getId(), tamar1);
        profileMap.put(shoshi1.getId(), shoshi1);

        archiveYosef = profileMap.put(yosef1.getId(), shoshi1);
        System.out.println(archiveYosef);

        System.out.println(profileMap.containsKey(yosef1.getId()));
        System.out.println(profileMap.get(yosef1.getId()));


        ProfileNew yosef2 = new ProfileNew(10, "Yosef Cohen", 30);
        ProfileNew tamar2 = new ProfileNew(2, "Tamar Levi", 40);
        ProfileNew shoshi2 = new ProfileNew(30, "Shoshi Weiss", 60.5);

        Map<Integer, ProfileNew> profileMapNew = new HashMap<>();
        ProfileNew archiveYosef1 = profileMapNew.put(yosef2.getId(), yosef2);
        profileMapNew.put(tamar2.getId(), tamar2);
        profileMapNew.put(shoshi2.getId(), shoshi2);

            /*
            public V computeIfAbsent(K key, Function<? super K, ? extends V> remappingFunction)
             */
        Map<String, Integer> newMap = new HashMap<>();
        newMap.put("1", 200);
        newMap.put("2", 400);
        newMap.put("3", 600);
        // value will not change because key "1" exists
        newMap.computeIfAbsent("1", key -> 200 + Integer.valueOf(key));
        // value is computed for the new key ("4")
        newMap.computeIfAbsent("4", key -> 200 + Integer.valueOf(key));
        System.out.println(newMap.get("4"));
        System.out.println(newMap.get("1"));

        for (Map.Entry<Integer, ProfileNew> entry : profileMapNew.entrySet()) { // entrySet (key-value pairs)
            System.out.println("Key = (" + entry.getKey() + "), Value = " + entry.getValue());
        }

    }
}
