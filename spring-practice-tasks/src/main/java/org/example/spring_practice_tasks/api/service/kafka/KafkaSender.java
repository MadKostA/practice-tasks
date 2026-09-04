package org.example.spring_practice_tasks.api.service.kafka;

public interface KafkaSender<K, T> {
    void sendMessage(K key, T value);
}
