package org.example.practice_tasks_1;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.TreeMap;

public class Warehouse {

//    10. Склад: класс Warehouse с методами receive(name, qty),
//    ship(name, qty) (вернуть false, если на складе не хватает),
//    printStock() - остатки в алфавитном порядке.
    private final Map<String, Integer> stock = new TreeMap();

    public void receive(String name, int qty) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name of stock is empty");
        }

        if (qty <= 0) {
            throw new IllegalArgumentException("Qty of stock must be greater than 0");
        }

        stock.merge(name, qty, Integer::sum);
    }

    public boolean ship(String name, int qty) {
        if (qty <= 0) {
            return false;
        }

        Integer current = stock.get(name);
        if (current != null && current >= qty) {
            int remainingStocks = current - qty;
            if (remainingStocks == 0) {
                stock.remove(name);
            } else {
                stock.put(name, remainingStocks);
            }

            return true;
        }

        return false;
    }

    public void printStock() {
        if (stock.isEmpty()) {
            System.out.println("Warehouse is empty.");
        } else {
            stock.entrySet()
                    .forEach(entry ->
                            System.out.println(entry.getKey() + ": " + entry.getValue()));
        }
    }
}
