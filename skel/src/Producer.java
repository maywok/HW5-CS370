package src;

public class Producer extends Thread {

    private Node src;

    public Producer(Node src) {
        this.src = src;
    }

    private Message produceMessage() {
        Long messageValue = src.generateMessage();
        return new Message(src.getNodeID(), src.selectDestination().getNodeID(), messageValue);
    }

    @Override
    public void run() {
        for (int i = 0; i < src.qtyMessagesToSend / src.producers.length; i++) {
            Message message = produceMessage();
            Node destination = src.selectDestination();

            // Try to send message to the destination's MessageBuffer
            try {
                destination.messageBuffer.emplaceMessage(message);
                src.updateSentMessages(message);
                logOutput(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logOutput(Message message) {
        System.out.println(src.getNodeID() + ": Sent " + message.messageValue + " to Node " + message.dst);
    }
}
