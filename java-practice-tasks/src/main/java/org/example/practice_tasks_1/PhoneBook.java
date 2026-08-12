package org.example.practice_tasks_1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class PhoneBook {

//    8.  Реализовать телефонную книгу: класс PhoneBook с методами
//    addNumber(name, number) (у человека может быть несколько номеров, без дублей),
//    getNumbers(name) (пустой список вместо null, наружу копия),
//    removeNumber(name, number) (если номеров не осталось - удалить запись целиком).
    private final Map<String, Set<String>> phoneBookMap = new HashMap();

    public void addNumber(String name, String number) {
        checkNameAndNumberValid(name, number);

        phoneBookMap.computeIfAbsent(name, (k) -> new HashSet()).add(number);
    }

    public List<String> getNumbers(String name) {
        Set<String> numbers = phoneBookMap.get(name);

        return numbers == null ? List.of() : List.copyOf(numbers);
    }

    public boolean removeNumber(String name, String number) {
        Set<String> numbers = phoneBookMap.get(name);
        if (numbers == null) {
            return false;
        }

        boolean isRemoved = numbers.remove(number);
        if (isRemoved && numbers.isEmpty()) {
            phoneBookMap.remove(name);
        }

        return isRemoved;
    }

    private static void checkNameAndNumberValid(String name, String number) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(number)) {
            throw new IllegalArgumentException("Name or phone is empty. Both must contain value");
        }
    }
}