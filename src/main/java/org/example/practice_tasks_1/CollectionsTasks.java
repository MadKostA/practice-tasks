package org.example.practice_tasks_1;

import java.util.*;
import java.util.Map.Entry;

public class CollectionsTasks {

//   3. Метод removeDuplicates(List list) - убрать дубликаты,
//   сохранив порядок первых вхождений. [3, 1, 3, 2, 1] дает [3, 1, 2].
    public static Set<Integer> removeDuplicates(List<Integer> list) {
        return listIsEmpty(list) ? Set.of() : new LinkedHashSet(list);
    }

//    4. Метод mergeSorted(List a, List b) - слить два
//    отсортированных списка в один отсортированный.
    public static List<Integer> mergeSorted(List<Integer> a, List<Integer> b) {
        if (listIsEmpty(a)) {
            return List.of();
        }

        if (listIsEmpty(b)) {
            return a;
        }

        a.addAll(b);
        a.sort(Integer::compareTo);
        return a;
    }

//    5. Метод wordFrequency(String text) - вывести топ-3 самых частых слова в строке.
    public static void wordFrequency(String text) {
        String[] splitedStringArray = text.split(" ");
        Map<String, Integer> countByStringMap = new HashMap();

        for (String string : splitedStringArray) {
            countByStringMap.compute(string,
                    (k, v) ->
                            v == null ? 1 : ++v);
        }

        countByStringMap.entrySet()
                .stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .forEach(entry ->
                        System.out.println(entry.getKey() + " " + entry.getValue()));
    }

    private static boolean listIsEmpty(List<Integer> list) {
        return list == null || list.isEmpty();
    }
}
