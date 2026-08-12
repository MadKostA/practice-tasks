package org.example.practice_tasks_2.idempotent_initialization;

//2) Идемпотентная инициализация. getResource() создаёт дорогой
// объект ровно один раз даже при гонке десятков потоков,
// дальше отдаёт готовый объект без блокировок.
public class LazyInitResource {
    private volatile ExpensiveResource resource;

    public ExpensiveResource getResource() {
        ExpensiveResource localRef = resource;
        if (localRef != null) {
            return localRef;
        }

        synchronized (this) {
            localRef = resource;
            if (localRef == null) {
                resource = localRef = new ExpensiveResource();
            }
            return localRef;
        }
    }
}

