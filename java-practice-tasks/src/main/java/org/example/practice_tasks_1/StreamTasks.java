package org.example.practice_tasks_1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamTasks {

//    11. Реализовать метод который находит сумму всех четных чисел списка.
    public static int sumEvenNumbers(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(Integer::intValue)
                .sum();
    }

//    12. Реализовать метод который принимает список и отдает список имен в верхнем регистре.
    public static List<String> toUpperCaseList(List<String> strings) {
        return strings.stream()
                .map(String::toUpperCase)
                .toList();
    }

//    13. Реализовать метод который возвращает кол-во строк в списке длиннее n символов.
    public static long countStringsLongerThan(List<String> strings, int n) {
        return strings.stream()
                .filter(s -> s.length() > n)
                .count();
    }

    private static class Employee {
        private String name;
        private int age;
        private BigDecimal salary;
        private String firm;

        public Employee(String name, int age, BigDecimal salary, String firm) {
            this.name = name;
            this.age = age;
            this.salary = salary;
            this.firm = firm;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public BigDecimal getSalary() {
            return salary;
        }

        public String getFirm() {
            return firm;
        }

        @Override
        public String toString() {
            return name + " (" + firm + ")";
        }
    }

//    14. Создать класс сотрудника с именем, возрастом, зп и фирмой(строка).
//    Реализовать метод который принимает список сотрудников и
//    отдает среднюю зп по каждой фирме (вернуть Map<String, BigDecimal>)
    public static Map<String, BigDecimal> averageSalaryByFirm(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getFirm,
                        Collectors.teeing(
                                Collectors.reducing(
                                        BigDecimal.ZERO, Employee::getSalary, BigDecimal::add),
                                Collectors.counting(),
                                (sum, count) -> sum.divide(
                                        BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                        )
                ));
    }

//    15. Используем класс сотрудника, метод принимает список сотрудников, вернуть первого сотрудника из фирмы X.
    public static Optional<Employee> findFirstEmployeeFromFirm(List<Employee> employees, String firm) {
        return employees.stream()
                .filter(e -> e.getFirm().equals(firm))
                .findFirst();
    }

}
