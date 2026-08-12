package org.example.practice_tasks_2;

import java.util.concurrent.Semaphore;

//3) Ограничитель одновременных вызовов.
// Метод apply() внутри секции (в методе) не больше N потоков, остальные ждут;
// лимит освобождается даже при исключении.
public class ConcurrencyLimiter {
    private final Semaphore semaphore;

    public ConcurrencyLimiter(int maxConcurrency) {
        if (maxConcurrency <= 0) {
            throw new IllegalArgumentException("maxConcurrency must be positive");
        }
        this.semaphore = new Semaphore(maxConcurrency);
    }

    public void apply(Runnable task) throws InterruptedException {
        semaphore.acquire();
        try {
            task.run();
        } catch (RuntimeException e) {
            System.out.println("Exception has been throws:" + e.getMessage());
        } finally {
            semaphore.release();
        }
    }
}