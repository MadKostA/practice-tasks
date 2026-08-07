package org.example.practice_tasks_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class AtomicMaximumTest {

    private AtomicMaximum atomicMaximum;

    private final Long BIGGER_LONG_VALUE = 100L;
    private final Long LOWER_LONG_VALUE = 50L;

    @BeforeEach
    void setUp() {
        atomicMaximum = new AtomicMaximum();
    }

    @Test
    void shouldReturnInitValue() {
        assertEquals(Long.MIN_VALUE, atomicMaximum.getMax());
    }

    @Test
    void shouldSetANewMaximumValue() {
        atomicMaximum.submit(BIGGER_LONG_VALUE);

        assertEquals(BIGGER_LONG_VALUE, atomicMaximum.getMax());
    }

    @Test
    void shouldLeftMaxAfterSubmittingLowerValue() {
        atomicMaximum.submit(BIGGER_LONG_VALUE);
        atomicMaximum.submit(LOWER_LONG_VALUE);

        assertEquals(BIGGER_LONG_VALUE, atomicMaximum.getMax());
    }

    @Test
    void shouldSetNewMaxValueAfterSubmittingLargerValue() {
        atomicMaximum.submit(LOWER_LONG_VALUE);
        atomicMaximum.submit(BIGGER_LONG_VALUE);

        assertEquals(BIGGER_LONG_VALUE, atomicMaximum.getMax());
    }

    @Test
    void shouldSetLongMaxValueAndKeepIt() {
        atomicMaximum.submit(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, atomicMaximum.getMax());

        atomicMaximum.submit(Long.MIN_VALUE);
        assertEquals(Long.MAX_VALUE, atomicMaximum.getMax());
    }

    @Test
    void shouldIncreaseMaxWhenThreadSubmitsLargerValue() throws Exception {
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);

        Future<?> f1 = executor.submit(() -> {
            barrier.await();
            atomicMaximum.submit(1);
            atomicMaximum.submit(60);
            return null;
        });

        Future<?> f2 = executor.submit(() -> {
            barrier.await();
            atomicMaximum.submit(7);
            atomicMaximum.submit(BIGGER_LONG_VALUE); // должен обновить максимум до 100
            return null;
        });

        Future<?> f3 = executor.submit(() -> {
            barrier.await();
            atomicMaximum.submit(20);
            atomicMaximum.submit(99);
            return null;
        });

        f1.get();
        f2.get();
        f3.get();
        executor.shutdown();

        assertEquals(BIGGER_LONG_VALUE, atomicMaximum.getMax());
    }
}