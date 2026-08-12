package org.example.practice_tasks_2;

import java.math.BigDecimal;
import java.util.Objects;

//5) Перевод без дедлока(считай банковский). Класс Account с полем long id
// и балансом; метод transfer(from, to, amount), вызываемый встречно из
// многих потоков (from/to меняются местами).
// Реализуй без дедлока, списание и зачисление атомарны.
public class Account {
    private final long id;
    private volatile BigDecimal balance;

    public Account(long id, BigDecimal initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public static void transfer(Account from, Account to, BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (from == to) {
            return;
        }

        // Упорядочиваем блокировки по id для предотвращения deadlock
        Account firstLock, secondLock;
        if (from.id < to.id) {
            firstLock = from;
            secondLock = to;
        } else if (from.id > to.id) {
            firstLock = to;
            secondLock = from;
        } else {
            // from.id == to.id, но from != to - нарушение уникальности id
            throw new IllegalStateException("Different accounts with same id: " + from.id);
        }

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.balance.compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient funds");
                }
                from.balance = from.balance.subtract(amount);
                to.balance = to.balance.add(amount);
            }
        }
    }
}
