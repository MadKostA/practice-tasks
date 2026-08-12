package org.example.practice_tasks_2.idempotent_initialization;

import java.util.concurrent.atomic.AtomicInteger;

public class ExpensiveResource {
    private static final AtomicInteger instanceCounter = new AtomicInteger(0);

    private final int id;

    public ExpensiveResource() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.id = instanceCounter.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    public static int getCreatedCount() {
        return instanceCounter.get();
    }

    public static void resetCounter() {
        instanceCounter.set(0);
    }
}
