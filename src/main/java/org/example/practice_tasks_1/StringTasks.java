package org.example.practice_tasks_1;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StringTasks {
    private static final Pattern p = Pattern.compile("\\p{Punct}|\\s");

//    1. Метод isPalindrome(String s) - палиндром без учета регистра и пробелов. "А роза упала на лапу Азора" дает true.
    public static boolean isPalindrome(String s) {
        if (StringUtils.isBlank(s)) {
            return false;
        }

        s = s.replaceAll(p.pattern(), "");
        s = s.toLowerCase();
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length / 2; ++i) {
            if (chars[i] != chars[chars.length - i - 1]) {
                return false;
            }
        }

        return true;
    }

//    2. Метод countChars(String s) - countChars("hello") дает {h=1, e=1, l=2, o=1}.
    public static Map<Character, Integer> countChars(String s) {
        if (StringUtils.isBlank(s)) {
            return Map.of();
        }

        Map<Character, Integer> countByCharacterMap = new HashMap();

        for (char c : s.toCharArray()) {
            countByCharacterMap.compute(c, (k, v) -> v == null ? 1 : ++v);
        }

        return countByCharacterMap;

    }
}
