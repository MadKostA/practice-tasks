package org.example.practice_tasks_2;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyLimiterTimeoutTest {

    @Test
    void shouldLimitConcurrentExecutions() throws Exception {
        final int maxConcurrency = 2;
        ConcurrencyLimiterTimeout limiter = new ConcurrencyLimiterTimeout(maxConcurrency);
        final int totalTasks = 5;
        ExecutorService executor = Executors.newFixedThreadPool(totalTasks);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalTasks);
        AtomicInteger current = new AtomicInteger(0);
        AtomicInteger maxObserved = new AtomicInteger(0);

        for (int i = 0; i < totalTasks; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    limiter.apply(() -> {
                        int cur = current.incrementAndGet();
                        maxObserved.updateAndGet(max -> Math.max(max, cur));
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } finally {
                            current.decrementAndGet();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(5, TimeUnit.SECONDS));
        executor.shutdownNow();

        assertEquals(maxConcurrency, maxObserved.get());
    }

    @Test
    void shouldReleaseLimitOnException() throws Exception {
        ConcurrencyLimiterTimeout limiter = new ConcurrencyLimiterTimeout(1);
        AtomicInteger counter = new AtomicInteger(0);
        Runnable failingTask = () -> {
            counter.incrementAndGet();
            throw new RuntimeException("Test exception");
        };

        assertThrows(RuntimeException.class, () -> limiter.apply(failingTask));
        assertEquals(1, counter.get());

        // Проверяем, что другой поток может зайти
        limiter.apply(counter::incrementAndGet);
        assertEquals(2, counter.get());
    }

    @Test
    void shouldRejectAfterTimeout() throws Exception {
        ConcurrencyLimiterTimeout limiter = new ConcurrencyLimiterTimeout(1);

        Thread blocker = new Thread(() -> {
            try {
                limiter.apply(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        blocker.start();

        assertThrows(RuntimeException.class, () -> limiter.apply(() -> {})); // Пытаемся выполнить задачу в другом потоке
    }

    @Test
    void shouldRejectNonPositiveMaxConcurrency() {
        assertThrows(IllegalArgumentException.class, () -> new ConcurrencyLimiterTimeout(0));
        assertThrows(IllegalArgumentException.class, () -> new ConcurrencyLimiterTimeout(-1));
    }
}