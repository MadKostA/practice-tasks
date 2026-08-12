package org.example.practice_tasks_2;

import java.util.concurrent.atomic.AtomicInteger;

//4) Graceful stop через volatile.
// Класс PollingWorker запускает фоновый поток, который
// в цикле делает работу(тик каждые 100 мс и принт номера тика),
// и метод stop() для корректной остановки.
public class PollingWorker {
    private volatile boolean running = false;
    private Thread workerThread;
    private final AtomicInteger tickCount = new AtomicInteger(0);

    /**
     * Запускает фоновый поток, который каждые 100 мс печатает номер тика.
     * Повторный вызов без остановки игнорируется.
     */
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        workerThread = new Thread(() -> {
            while (running) {
                int currentTick = tickCount.incrementAndGet();
                System.out.println("Tick: " + currentTick);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Поток был прерван для ускорения остановки.
                    // Восстанавливаем статус прерывания и выходим из сна.
                    Thread.currentThread().interrupt();
                    // Следующая итерация проверит running == false и завершит цикл.
                }
            }
            System.out.println("Worker thread stopped.");
        });
        workerThread.start();
    }

    public void stop() {
        running = false;
        if (workerThread != null) {
            workerThread.interrupt();
            try {
                workerThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Даже если текущий поток прерван, флаг running уже сброшен,
                // и воркер завершится самостоятельно.
            }
        }
    }

    public int getTickCount() {
        return tickCount.get();
    }

    public boolean isRunning() {
        return workerThread != null && workerThread.isAlive();
    }
}