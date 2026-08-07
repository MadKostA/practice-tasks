package org.example.practice_tasks_2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LockFreeStackTest {

    @Test
    void pushAndPopSingleElement() {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        stack.push(42);
        assertEquals(42, stack.pop());
        assertNull(stack.pop());
    }

    @Test
    void lifoOrder() {
        LockFreeStack<String> stack = new LockFreeStack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
    }

    @Test
    void popFromEmptyReturnsNull() {
        LockFreeStack<Object> stack = new LockFreeStack<>();
        assertNull(stack.pop());
        assertNull(stack.pop());
    }

    @Test
    void peekReturnsTopWithoutRemoving() {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        stack.push(10);
        stack.push(20);
        assertEquals(20, stack.peek());
        assertEquals(20, stack.peek());
        assertEquals(20, stack.pop());
        assertEquals(10, stack.peek());
    }

    @Test
    void isEmptyWorks() {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
        stack.pop();
        assertTrue(stack.isEmpty());
    }

    @Test
    void concurrentPushThenPop() throws InterruptedException {
        final int THREADS = 4;
        final int PER_THREAD = 1000;
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        // Параллельное добавление
        for (int t = 0; t < THREADS; t++) {
            final int start = t * PER_THREAD;
            executor.submit(() -> {
                for (int i = 0; i < PER_THREAD; i++) {
                    stack.push(start + i);
                }
            });
        }
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Подсчитываем все извлечённые элементы
        Set<Integer> poppedElements = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Integer val;
        while ((val = stack.pop()) != null) {
            assertTrue(poppedElements.add(val));
        }
        assertEquals(THREADS * PER_THREAD, poppedElements.size());
    }
}