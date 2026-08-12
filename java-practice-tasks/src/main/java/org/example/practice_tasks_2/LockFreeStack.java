package org.example.practice_tasks_2;

import java.util.concurrent.atomic.AtomicReference;
import java.util.NoSuchElementException;

//9) Реализуй неблокирующий стек push/pop через
// AtomicReference и CAS-цикл, без synchronized.
public class LockFreeStack<T> {

    /*Узел стека неизменяемый, содержит значение и ссылку на следующий узел.*/
    private static class Node<T> {
        final T value;
        final Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    /*Вершина стека - атомарная ссылка.*/
    private final AtomicReference<Node<T>> top = new AtomicReference<>(null);

    /*Помещает элемент на вершину стека. Не блокируется, использует CAS-цикл.*/
    public void push(T value) {
        Node<T> newNode;
        Node<T> currentTop;
        do {
            currentTop = top.get();
            newNode = new Node<>(value, currentTop);
        } while (!top.compareAndSet(currentTop, newNode));
    }

    /*Снимает элемент с вершины стека. Возвращает null, если стек пуст. Использует CAS-цикл.*/
    public T pop() {
        Node<T> currentTop;
        Node<T> newTop;
        do {
            currentTop = top.get();
            if (currentTop == null) {
                return null;
            }
            newTop = currentTop.next;
        } while (!top.compareAndSet(currentTop, newTop));
        return currentTop.value;
    }

    /*Возвращает значение на вершине стека без удаления, либо null.*/
    public T peek() {
        Node<T> currentTop = top.get();
        return currentTop == null ? null : currentTop.value;
    }

    /*Проверяет, пуст ли стек. Состояние может измениться сразу после вызова.*/
    public boolean isEmpty() {
        return top.get() == null;
    }
}
