package practice_tasks_2;

import static org.junit.jupiter.api.Assertions.*;

import org.example.practice_tasks_2.ParallelArraySummator;
import org.junit.jupiter.api.*;
import java.util.Random;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ParallelArraySummatorTest {

    private ParallelArraySummator summator;
    private final int K = 4;

    @BeforeEach
    void setUp() {
        summator = new ParallelArraySummator(K);
    }

    @AfterEach
    void tearDown() {
        summator.close();
    }

    @Test
    void shouldSumSingleArray() throws Exception {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        long sum = summator.processArray(array);

        assertEquals(55, sum);
    }

    @Test
    void shouldReuseThreadsAndBarrierForMultipleArrays() throws Exception {
        int[] array1 = {10, 20, 30};
        int[] array2 = {1, 2, 3, 4, 5};

        long sum1 = summator.processArray(array1);
        long sum2 = summator.processArray(array2);

        assertEquals(60, sum1);
        assertEquals(15, sum2);
    }

    @Test
    void shouldHandleEmptyArray() throws Exception {
        assertEquals(0, summator.processArray(new int[0]));
    }

    @Test
    void shouldWorkWithLargeRandomArray() throws Exception {
        int size = 10_000;
        int[] array = new Random(42)
                .ints(size, -100, 100)
                .toArray();
        long expectedSum = 0;

        for (int val : array)
            expectedSum += val;
        long actualSum = summator.processArray(array);

        assertEquals(expectedSum, actualSum);
    }

    @Test
    void shouldWorkWithKEqualsOne() throws Exception {
        ParallelArraySummator s = new ParallelArraySummator(1);

        try {
            assertEquals(30, s.processArray(new int[]{5, 10, 15}));
        } finally {
            s.close();
        }
    }

    @Test
    void shouldWorkWhenKLargerThanArrayLength() throws Exception {
        ParallelArraySummator s = new ParallelArraySummator(10);

        try {
            assertEquals(6, s.processArray(new int[]{1, 2, 3}));
        } finally {
            s.close();
        }
    }

    @Test
    void shouldBeThreadSafeUnderConcurrentReuse() throws Exception {
        ParallelArraySummator s = new ParallelArraySummator(8);

        try {
            Random rnd = new Random(123);

            for (int iter = 0; iter < 200; iter++) {
                int[] arr = rnd.ints(500, -50, 50)
                        .toArray();
                long expectedSum = 0;
                for (int v : arr)
                    expectedSum += v;

                long actualSum = s.processArray(arr);

                assertEquals(expectedSum, actualSum);
            }
        } finally {
            s.close();
        }
    }
}