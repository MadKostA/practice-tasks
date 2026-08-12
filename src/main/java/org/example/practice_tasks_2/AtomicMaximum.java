package org.example.practice_tasks_2;

import java.util.concurrent.atomic.AtomicLong;

//1) Атомарный максимум. submit(long) из N потоков хранит максимум,
// getMax() отдаёт без блокировок.
public class AtomicMaximum {

    private AtomicLong max = new AtomicLong(Long.MIN_VALUE);

    public void submit(long value) {
        max.updateAndGet(current -> Math.max(current, value));
    }

    public long getMax() {
        return max.get();
    }

}
