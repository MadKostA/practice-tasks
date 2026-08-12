package practice_tasks_2;

import org.example.practice_tasks_2.PollingWorker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PollingWorkerTest {

    @Test
    void shouldSurviveStopWithoutStart() {
        PollingWorker worker = new PollingWorker();
        assertDoesNotThrow(worker::stop);
    }

    @Test
    void workerThreadShouldTerminateAfterStop() throws InterruptedException {
        PollingWorker worker = new PollingWorker();
        worker.start();
        Thread.sleep(200);
        worker.stop();
        assertFalse(worker.isRunning());
    }

    @Test
    void shouldAccumulatePositiveNumberOfTicks() throws InterruptedException {
        PollingWorker worker = new PollingWorker();
        worker.start();
        Thread.sleep(200);
        worker.stop();
        assertTrue(worker.getTickCount() > 0);
    }

    @Test
    void shouldIncrementTicksAndStopGracefully() throws InterruptedException {
        PollingWorker worker = new PollingWorker();
        worker.start();

        // Должно накопиться минимум 3 тика
        Thread.sleep(350);
        int ticksBeforeStop = worker.getTickCount();
        assertTrue(ticksBeforeStop >= 3);

        worker.stop();

        int ticksAfterStop = worker.getTickCount();
        // Допускаем не более одного дополнительного тика
        assertTrue(ticksAfterStop <= ticksBeforeStop + 1);

        assertFalse(worker.isRunning());
    }

    @Test
    void shouldHandleMultipleStopCalls() throws InterruptedException {
        PollingWorker worker = new PollingWorker();
        worker.start();
        Thread.sleep(150);
        worker.stop();
        worker.stop();
        assertFalse(worker.isRunning());
    }
}