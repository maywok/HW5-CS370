package src;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Node extends Thread {

    private String nodeld;
    private Random mg;
    private boolean done = false;
    public Long qtyMessagesToSend;
    private Node[] neighbors;
    public Producer[] producers;
    private Consumer[] consumers;
    public MessageBuffer messageBuffer;
    private AtomicLong totalMessagesSent = new AtomicLong(0);
    private AtomicLong totalMessagesReceived = new AtomicLong(0);
    private AtomicLong sumOfMessagesSent = new AtomicLong(0);
    private AtomicLong sumOfMessagesReceived = new AtomicLong(0);

    public Node(String nodeld, Long qtyMessagesToSend, Long seed, Integer numNeighbors, Integer bufferSize) {
        this.nodeld = nodeld;
        this.mg = new Random(seed + Long.parseLong(nodeld));
        this.qtyMessagesToSend = qtyMessagesToSend;
        this.messageBuffer = new MessageBuffer(bufferSize);
        this.producers = new Producer[numNeighbors];
        this.consumers = new Consumer[numNeighbors];

        // Initialize Producer and Consumer threads
        for (int i = 0; i < numNeighbors; i++) {
            producers[i] = new Producer(this);
            consumers[i] = new Consumer(this);
        }
    }

    public String getNodeID() {
        return nodeld;
    }

    public void setNeighbors(Node[] neighbors) {
        this.neighbors = neighbors;
    }

    public Long generateMessage() {
        return (long) (mg.nextInt(1023) + 1);  // Random message value between 1 and 1024
    }

    public Node selectDestination() {
        int neighborIndex = mg.nextInt(neighbors.length);
        return neighbors[neighborIndex];
    }

    public void updateSentMessages(Message message) {
        totalMessagesSent.incrementAndGet();
        sumOfMessagesSent.addAndGet(message.messageValue);
    }

    public void updateReceivedMessages(Message message) {
        totalMessagesReceived.incrementAndGet();
        sumOfMessagesReceived.addAndGet(message.messageValue);
    }

    public Long reportTotalSent() {
        return totalMessagesSent.get();
    }

    public Long reportTotalReceived() {
        return totalMessagesReceived.get();
    }

    public Long reportSumSent() {
        return sumOfMessagesSent.get();
    }

    public Long reportSumReceived() {
        return sumOfMessagesReceived.get();
    }

    public boolean checkDone() {
        return done;
    }

    @Override
    public void run() {
        // Start producer and consumer threads
        for (Producer producer : producers) {
            producer.start();
        }
        for (Consumer consumer : consumers) {
            consumer.start();
        }

        // Wait for all messages to be sent
        for (Producer producer : producers) {
            try {
                producer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        done = true;
    }
}
