package practice_tasks_2.producer_consumer;

import org.example.practice_tasks_2.producer_consumer.Consumer;
import org.example.practice_tasks_2.producer_consumer.PoisonPillExample;
import org.example.practice_tasks_2.producer_consumer.Producer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PoisonPillExampleTest {

    private final String POISON_PILL = "POISON_PILL";

    @Test
    void shouldProcessAllTasksWithoutLoss() throws InterruptedException {
        int numProducers = 2;
        int numConsumers = 3;
        int tasksPerProducer = 50;

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);

        // Запускаем продюсеров
        Thread[] producers = new Thread[numProducers];
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(
                    new Producer(queue, tasksPerProducer, producedCount)
            );
            producers[i].start();
        }

        // Запускаем консьюмеров
        Thread[] consumers = new Thread[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(
                    new Consumer(queue, consumedCount)
            );
            consumers[i].start();
        }

        // Дожидаемся продюсеров
        for (Thread p : producers) {
            p.join();
        }

        // Отправляем таблетки
        for (int i = 0; i < numConsumers; i++) {
            queue.put(POISON_PILL);
        }

        // Дожидаемся консьюмеров
        for (Thread c : consumers) {
            c.join(10_000);
            assertFalse(c.isAlive());
        }

        // Все задачи должны быть обработаны
        assertEquals(producedCount.get(), consumedCount.get());
        assertEquals(numProducers * tasksPerProducer, consumedCount.get());
    }

    @Test
    void shouldStopWithoutHangingWhenNoTasks() throws InterruptedException {
        int numConsumers = 3;
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        AtomicInteger consumedCount = new AtomicInteger(0);

        // Запускаем консьюмеров без продюсеров
        Thread[] consumers = new Thread[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(new Consumer(queue, consumedCount));
            consumers[i].start();
        }

        for (int i = 0; i < numConsumers; i++) {
            queue.put(PoisonPillExample.POISON_PILL);
        }

        for (Thread c : consumers) {
            c.join(2_000);
            assertFalse(c.isAlive());
        }

        assertEquals(0, consumedCount.get());
    }
}