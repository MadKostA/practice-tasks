package org.example.practice_tasks_1;

import java.util.List;
import java.util.stream.IntStream;

public class Report {

//    7.  Сборка отчета из 100 000 строк двумя способами:
//    метод joinWithPlus (через += в цикле) и joinWithBuilder (через StringBuilder.append).
//    Замерь время каждого через System.currentTimeMillis и сравни.
    private static final List<String> reportList = IntStream.range(0, 100000)
            .mapToObj((i) -> "строка_" + i + "\n").toList();

    public static void joinWithPlus() {
        String joinedReport = "";
        long start = System.currentTimeMillis();

        for(String s : reportList) {
            joinedReport = joinedReport + s;
        }

        long finish = System.currentTimeMillis();
        System.out.println("Time execution of joinWithPlus: " + (finish - start));
    }

    public static void joinWithBuilder() {
        StringBuilder joinedReport = new StringBuilder();
        long start = System.currentTimeMillis();

        for(String s : reportList) {
            joinedReport.append(s);
        }

        long finish = System.currentTimeMillis();
        System.out.println("Time execution of joinWithBuilder: " + (finish - start));
    }
}
