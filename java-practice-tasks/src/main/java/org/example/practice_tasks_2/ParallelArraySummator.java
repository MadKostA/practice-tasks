package org.example.practice_tasks_2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

//7) Раздели массив на K частей, K потоков считают частичную сумму своей части,
// на барьере сводят в общий итог, печатают;
// для демонстрации переиспользования повтори на втором массиве.
public class ParallelArraySummator implements AutoCloseable {

    private final ExecutorService executor;
    private final CyclicBarrier barrier;
    private final int K;
    private volatile long[] partialSums;
    private final AtomicLong totalResult = new AtomicLong();

    public ParallelArraySummator(int K) {
        if (K <= 0)
            throw new IllegalArgumentException("K must be > 0");
        this.K = K;
        this.executor = Executors.newFixedThreadPool(K);
        this.barrier = new CyclicBarrier(K + 1, () -> {
            long total = 0;
            for (long s : partialSums) {
                total += s;
            }
            totalResult.set(total);
            System.out.println("Total sum: " + total);
        });
    }

    public long processArray(int[] array) throws InterruptedException, BrokenBarrierException {
        partialSums = new long[K];
        int n = array.length;
        int chunkSize = n / K;
        int remainder = n % K;
        int start = 0;

        for (int i = 0; i < K; i++) {
            final int idx = i;
            int end = start + chunkSize + (i < remainder ? 1 : 0);
            int finalStart = start;
            int finalEnd = end;

            executor.submit(() -> {
                long sum = 0;
                for (int j = finalStart; j < finalEnd; j++) {
                    sum += array[j];
                }
                partialSums[idx] = sum;
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
            start = end;
        }

        barrier.await(); // главный поток тоже входит в барьер
        return totalResult.get();
    }

    @Override
    public void close() {
        executor.shutdown();
    }
}
