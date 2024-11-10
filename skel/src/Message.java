package src;

public class Message {
    public String src;
    public String dst;
    public Long messageValue;

    public Message(String src, String dst, Long messageValue) {
        this.src = src;
        this.dst = dst;
        this.messageValue = messageValue;
    }

}
