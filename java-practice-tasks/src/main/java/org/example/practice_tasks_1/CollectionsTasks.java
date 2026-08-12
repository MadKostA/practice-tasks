package org.example.practice_tasks_1;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CollectionsTasks {

//   3. Метод removeDuplicates(List list) - убрать дубликаты,
//   сохранив порядок первых вхождений. [3, 1, 3, 2, 1] дает [3, 1, 2].
    public static List<Integer> removeDuplicates(List<Integer> list) {
        return listIsEmpty(list)
                ? List.of()
                : new LinkedHashSet<>(list).stream().toList();
    }

//    4. Метод mergeSorted(List a, List b) - слить два
//    отсортированных списка в один отсортированный.
    public static List<Integer> mergeSorted(List<Integer> a, List<Integer> b) {
        if (a == null && b == null) {
            return List.of();
        } else if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        }

        a.addAll(b);
        a.sort(Integer::compareTo);
        return a;
    }

//    5. Метод wordFrequency(String text) - вывести топ-3 самых частых слова в строке.
    public static Map<String, Integer> wordFrequency(String text) {
        if (StringUtils.isBlank(text)) {
            return Map.of();
        }

        String trimmedText = text.trim();
        String[] splitStringArray = trimmedText.split(" ");
        Map<String, Integer> countByStringMap = new HashMap();

        for (String string : splitStringArray) {
            if (string.isBlank()) continue;

            countByStringMap.compute(string,
                    (k, v) ->
                            v == null ? 1 : ++v);
        }

        return countByStringMap.entrySet()
                .stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static boolean listIsEmpty(List<Integer> list) {
        return list == null || list.isEmpty();
    }
}
