package src;

public class Consumer extends Thread {

    private Node src;

    public Consumer(Node src) {
        this.src = src;
    }

    @Override
    public void run() {
        while (!src.checkDone() || src.messageBuffer.count > 0) {
            try {
                Message message = src.messageBuffer.pollMessage();
                if (message != null) {
                    src.updateReceivedMessages(message);
                    logInput(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logInput(Message message) {
        System.out.println(src.getNodeID() + ": Received " + message.messageValue + " from Node " + message.src);
    }
}
