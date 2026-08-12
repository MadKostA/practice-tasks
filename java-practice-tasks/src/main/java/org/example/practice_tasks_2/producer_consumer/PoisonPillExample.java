package org.example.practice_tasks_2.producer_consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class PoisonPillExample {

    public static final String POISON_PILL = "POISON_PILL";

    public static void main(String[] args) throws InterruptedException {
        int numProducers = 2;
        int numConsumers = 3;
        int tasksPerProducer = 10;

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);

        Thread[] producers = new Thread[numProducers];
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(
                    new Producer(queue, tasksPerProducer, producedCount),
                    "Producer-" + i
            );
            producers[i].start();
        }

        Thread[] consumers = new Thread[numConsumers];
        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(
                    new Consumer(queue, consumedCount),
                    "Consumer-" + i
            );
            consumers[i].start();
        }

        for (Thread p : producers) {
            p.join();
        }

        for (int i = 0; i < numConsumers; i++) {
            queue.put(POISON_PILL);
        }

        for (Thread c : consumers) {
            c.join();
        }

        System.out.println("Произведено задач: " + producedCount.get());
        System.out.println("Обработано задач: " + consumedCount.get());
        System.out.println("Все задачи обработаны, система остановлена.");
    }
}
