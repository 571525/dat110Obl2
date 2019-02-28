package no.hvl.dat110.messages;

public class UnsubscribeMsg extends Message {


    // TODO: 
    // Implement objectvariables, constructor, get/set-methods, and toString method

    private String topic;

    public UnsubscribeMsg(String user, String topic) {
        super(MessageType.UNSUBSCRIBE, user);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "MessageType: " + getType() + ", user: " + getUser() + ", " +
                "topic='" + topic + '\'' +
                '}';
    }
}
