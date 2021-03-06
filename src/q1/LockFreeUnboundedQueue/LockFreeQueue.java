package q1.LockFreeUnboundedQueue;

import java.util.concurrent.atomic.AtomicReference;

// implementation of an unbounded, thread-safe non-blocking queue
class LockFreeQueue<T> {
    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    LockFreeQueue() {
        Node<T> sentinel = new Node<>(null);
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    // adds an item to the front of the queue
    void enq(T value) {
        Node<T> node = new Node<>(value);
        while (true) {
            Node<T> last = tail.get();
            Node<T> next = last.getNext().get();
            if (last == tail.get()) {
                if (next == null) {
                    if (last.getNext().compareAndSet(null, node)) {
                        tail.compareAndSet(last, node);
                        node.setEnqTime(System.currentTimeMillis());
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    // remove the item at the end of the queue
    Node<T> deq() throws Exception {
        while(true) {
            Node<T> first = head.get();
            Node<T> last = tail.get();
            Node<T> next = first.getNext().get();
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        throw new Exception();
                    }
                    tail.compareAndSet(last, next);
                } else {
                    if (head.compareAndSet(first, next)) {
                        next.setDeqTime(System.currentTimeMillis());
                        return next;
                    }
                }
            }
        }
    }
}
