package org.example.practice_tasks_1;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class SiteStats {

//    9. Статистика посещений сайта: класс SiteStats с методами
//    visit(LocalDate date, String userId),
//    uniqueVisitors(date) (сколько разных пользователей за день),
//    wasUserOn(date, userId).
    private final Map<LocalDate, Set<String>> usersByDayMap = new HashMap();

    public void visit(LocalDate date, String userId) {
        checkIfDateOrUserIdNotValid(date, userId);

        usersByDayMap.computeIfAbsent(date, (k) -> new HashSet()).add(userId);
    }

    public long uniqueVisitors(LocalDate date) {
        Set<String> users = usersByDayMap.get(date);
        return users == null ? 0 : users.size();
    }

    public boolean wasUserOn(LocalDate date, String userId) {
        checkIfDateOrUserIdNotValid(date, userId);
        Set<String> users = usersByDayMap.get(date);

        return users != null && users.contains(userId);
    }

    private void checkIfDateOrUserIdNotValid(LocalDate date, String userId) {
        if (date == null || StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("Date or userId is empty");
        }
    }
}