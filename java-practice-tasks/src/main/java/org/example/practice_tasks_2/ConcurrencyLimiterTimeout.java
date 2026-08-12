package org.example.practice_tasks_2;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//8) Доработай ограничитель из задачи 3: если за 200 мс слот не
// освободился - не ждать дальше, а вернуть быстрый отказ (как 503), не уходя в бесконечное ожидание.
public class ConcurrencyLimiterTimeout extends ConcurrencyLimiter{
    private static final long TIMEOUT_MS = 200;
    private final Semaphore semaphore;

    public ConcurrencyLimiterTimeout(int maxConcurrency) {
        super(maxConcurrency);
        this.semaphore = new Semaphore(maxConcurrency);
    }

    public void apply(Runnable task) throws InterruptedException {
        applyInternal(task, TIMEOUT_MS);
    }

    private void applyInternal(Runnable task, long timeoutMs) throws InterruptedException {
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new RuntimeException("Too many requests – try again later");
            }
            task.run();
        } finally {
            if (acquired) {
                semaphore.release();
            }
        }
    }
}
