package no.hvl.dat110.broker;

import no.hvl.dat110.messages.PublishMsg;
import no.hvl.dat110.messagetransport.Connection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {

    protected ConcurrentHashMap<String, Set<String>> subscriptions;
    protected ConcurrentHashMap<String, ClientSession> clients;
    protected ConcurrentHashMap<String, List<PublishMsg>> bufferedMessages; // clients mapped to list of messages

    public Storage() {
        subscriptions = new ConcurrentHashMap<String, Set<String>>();
        clients = new ConcurrentHashMap<String, ClientSession>();
        bufferedMessages = new ConcurrentHashMap<String, List<PublishMsg>>(); //maps users to buffered messages
    }

    public Collection<ClientSession> getSessions() {
        return clients.values();
    }

    public Set<String> getTopics() {

        return subscriptions.keySet();

    }

    public ClientSession getSession(String user) {

        ClientSession session = clients.get(user);

        return session;
    }

    public Set<String> getSubscribers(String topic) {

        return (subscriptions.get(topic));

    }

    public void addClientSession(String user, Connection connection) {

        // TODO: add corresponding client session to the storage
        ClientSession session = new ClientSession(user, connection);
        clients.put(user, session);
    }

    public void removeClientSession(String user) {

        // TODO: remove client session for user from the storage
        clients.remove(user);
    }

    public void createTopic(String topic) {

        // TODO: create topic in the storage
        subscriptions.put(topic, new HashSet<>());
    }

    public void deleteTopic(String topic) {

        // TODO: delete topic from the storage
        subscriptions.remove(topic);
    }

    public void addSubscriber(String user, String topic) {

        // TODO: add the user as subscriber to the topic
        subscriptions.get(topic).add(user);
    }

    public void removeSubscriber(String user, String topic) {

        // TODO: remove the user as subscriber to the topic
        subscriptions.get(topic).remove(user);

    }

    public void bufferMessage(String user, PublishMsg message) {
        if(bufferedMessages.get(user) != null)
            bufferedMessages.get(user).add(message);
    }

    public void startBufferingMessages(String user) {
        bufferedMessages.put(user, new ArrayList<PublishMsg>());
    }

    public void stopBufferingMessages(String user) {
        bufferedMessages.remove(user);
    }

    public List<PublishMsg> getBufferedMessages(String user) {
        return bufferedMessages.get(user);
    }
}
