package no.hvl.dat110.messages;

// TODO:
// Implement objectvariables, constructor, get/set-methods, and toString method

public class CreateTopicMsg extends Message {

    private String user;
    private String topic;
    private String message;

    public CreateTopicMsg(String user, String topic) {
        this.user = user;
        this.topic = topic;
        message="";
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
