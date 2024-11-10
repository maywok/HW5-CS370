package src;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageBuffer {

    private Message[] internalMessageBuffer;
    private int bufferCapacity;
    public int front, rear, count;
    private ReentrantLock lock;
    private Condition notFull, notEmpty;

    public MessageBuffer(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
        internalMessageBuffer = new Message[bufferCapacity];
        front = 0;
        rear = 0;
        count = 0;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void emplaceMessage(Message message) throws InterruptedException {
        lock.lock();
        try {
            while (count == bufferCapacity) {
                notFull.await();  // Wait if buffer is full
            }
            internalMessageBuffer[rear] = message;
            rear = (rear + 1) % bufferCapacity;
            count++;
            notEmpty.signal();  // Signal that buffer is not empty
        } finally {
            lock.unlock();
        }
    }

    public Message pollMessage() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();  // Wait if buffer is empty
            }
            Message message = internalMessageBuffer[front];
            front = (front + 1) % bufferCapacity;
            count--;
            notFull.signal();  // Signal that buffer is not full
            return message;
        } finally {
            lock.unlock();
        }
    }
}
