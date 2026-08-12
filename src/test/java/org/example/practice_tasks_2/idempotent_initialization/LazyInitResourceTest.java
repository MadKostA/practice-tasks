package org.example.practice_tasks_2.idempotent_initialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class LazyInitResourceTest {

    @BeforeEach
    void resetCounters() {
        ExpensiveResource.resetCounter();
    }

    @Test
    void shouldReturnSameInstanceAfterInitialization() {
        LazyInitResource holder = new LazyInitResource();
        ExpensiveResource first = holder.getResource();
        ExpensiveResource second = holder.getResource();
        assertSame(first, second, "Повторный вызов должен вернуть тот же объект");
    }

    @Test
    void shouldCreateExactlyOneInstanceUnderHighConcurrency() throws InterruptedException {
        final int threadCount = 100;
        LazyInitResource holder = new LazyInitResource();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<ExpensiveResource> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    ExpensiveResource resource = holder.getResource();
                    results.add(resource);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        ExpensiveResource first = results.get(0);
        for (ExpensiveResource r : results) {
            assertSame(first, r, "Все потоки должны получить одинаковый экземпляр");
        }

        assertEquals(1, ExpensiveResource.getCreatedCount(),
                "Конструктор дорогого объекта должен вызываться только один раз");
    }
}