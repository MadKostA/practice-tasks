package org.example.practice_tasks_2.producer_consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

//6) Producer-consumer с poison pill. 2 продюсера кладут задачи (String) в очередь,
// 3 консьюмера разбирают. Останови систему корректно (без потерь и без вечного ожидания).
// Консюмеры не знают сколько задач должны разобрать.
public class Producer implements Runnable {
    private final BlockingQueue<String> queue;
    private final int numTasks;
    private final AtomicInteger producedCount;

    public Producer(BlockingQueue<String> queue, int numTasks, AtomicInteger producedCount) {
        this.queue = queue;
        this.numTasks = numTasks;
        this.producedCount = producedCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < numTasks; i++) {
                String task = "Task-" + Thread.currentThread().getName() + "-" + i;
                queue.put(task);
                producedCount.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
