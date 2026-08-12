package org.example.practice_tasks_2.producer_consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Консьюмер – забирает задачи из очереди, пока не встретит POISON_PILL.
 */
public class Consumer implements Runnable {
    private final BlockingQueue<String> queue;
    private final AtomicInteger consumedCount;

    private final String POISON_PILL = "POISON_PILL";

    public Consumer(BlockingQueue<String> queue, AtomicInteger consumedCount) {
        this.queue = queue;
        this.consumedCount = consumedCount;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String task = queue.take();
                if (POISON_PILL.equals(task)) {
                    break;
                }
                process(task);
                consumedCount.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void process(String task) throws InterruptedException {
        // Имитация обработки задачи (в реальном коде – полезная работа)
        Thread.sleep(100);
        System.out.println(Thread.currentThread().getName() + " обрабатывает " + task);
    }
}
