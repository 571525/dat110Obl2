package no.hvl.dat110.messages;

// TODO:
// Implement objectvariables, constructor, get/set-methods, and toString method

public class CreateTopicMsg extends Message {

    private String topic;

    public CreateTopicMsg(String user, String topic) {
        super(MessageType.CREATETOPIC, user);
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
        return getType() +
                "topic='" + topic + '\'' + " user: " + getUser() +
                '}';
    }
}
