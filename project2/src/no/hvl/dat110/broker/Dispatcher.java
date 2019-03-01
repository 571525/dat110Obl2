package no.hvl.dat110.broker;

import no.hvl.dat110.common.Logger;
import no.hvl.dat110.common.Stopable;
import no.hvl.dat110.messages.*;
import no.hvl.dat110.messagetransport.Connection;

import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher extends Stopable {

    private Storage storage;
    private ConcurrentHashMap<ClientSession, Stopable> clientThreads;

    public Dispatcher(Storage storage) {
        super("Dispatcher");
        this.storage = storage;
        this.clientThreads = new ConcurrentHashMap<ClientSession, Stopable>();
    }

    @Override
    public void doProcess() {

        Logger.lg(".");

        // this thread joins all the client threads
        storage.getSessions().forEach(a -> {
            try {
                clientThreads.get(a).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public void dispatch(ClientSession client, Message msg) {

        MessageType type = msg.getType();

        switch (type) {

            case DISCONNECT:
                onDisconnect((DisconnectMsg) msg);
                break;

            case CREATETOPIC:
                onCreateTopic((CreateTopicMsg) msg);
                break;

            case DELETETOPIC:
                onDeleteTopic((DeleteTopicMsg) msg);
                break;

            case SUBSCRIBE:
                onSubscribe((SubscribeMsg) msg);
                break;

            case UNSUBSCRIBE:
                onUnsubscribe((UnsubscribeMsg) msg);
                break;

            case PUBLISH:
                onPublish((PublishMsg) msg);
                bufferMessage((PublishMsg) msg); //buffers message if needed
                break;


            default:
                Logger.log("broker dispatch - unhandled message type");
                break;

        }
    }

    // called from Broker after having established the underlying connection
    public void onConnect(ConnectMsg msg, Connection connection) {

        String user = msg.getUser();

        Logger.log("onConnect:" + msg.toString());

        storage.addClientSession(user, connection);

        Stopable thread = new Stopable(user + " thread") {
            @Override
            public void doProcess() {
                ClientSession client = storage.getSession(user);

                try {
                    Message message = null;
                    if (client.hasData()) {
                        message = client.receive();
                    }

                    if (message != null) {
                        dispatch(client, message);
                    }
                }catch (Exception e) {
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
        clientThreads.put(storage.getSession(user), thread);

        // Send buffered messages after connect/reconnect
        if (storage.getBufferedMessages(user) != null) {
            // give time for clientsession to be registered in broker
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            storage.getBufferedMessages(user).forEach(a -> storage.getSession(user).send(a));
            storage.stopBufferingMessages(user);
        }

    }

    // called by dispatch upon receiving a disconnect message
    public void onDisconnect(DisconnectMsg msg) {

        String user = msg.getUser();

        Logger.log("onDisconnect:" + msg.toString());

        clientThreads.remove(storage.getSession(user)).doStop();
        storage.removeClientSession(user);
        storage.startBufferingMessages(user);
    }


    public void onCreateTopic(CreateTopicMsg msg) {

        Logger.log("onCreateTopic:" + msg.toString());

        // TODO: create the topic in the broker storage
        storage.createTopic(msg.getTopic());
    }

    public void onDeleteTopic(DeleteTopicMsg msg) {

        Logger.log("onDeleteTopic:" + msg.toString());

        // TODO: delete the topic from the broker storage
        storage.deleteTopic(msg.getTopic());
    }

    public void onSubscribe(SubscribeMsg msg) {

        Logger.log("onSubscribe:" + msg.toString());

        // TODO: subscribe user to the topic
        storage.addSubscriber(msg.getUser(), msg.getTopic());

    }

    public void onUnsubscribe(UnsubscribeMsg msg) {

        Logger.log("onUnsubscribe:" + msg.toString());

        // TODO: unsubscribe user to the topic
        storage.removeSubscriber(msg.getUser(), msg.getTopic());

    }

    public void onPublish(PublishMsg msg) {

        Logger.log("onPublish:" + msg.toString());

        // TODO: publish the message to clients subscribed to the topic
        storage.getSubscribers(msg.getTopic())
                .stream()
                .filter(a -> storage.getSession(a) != null)
                .forEach(a -> storage.getSession(a).send(msg));
    }

    /**
     * Adds the message to each user currently buffering messages
     *
     * @param msg
     */
    private void bufferMessage(PublishMsg msg) {
        String topic = msg.getTopic();

        storage.getSubscribers(topic)
                .stream()
                .filter(a -> storage.getBufferedMessages(a) != null)
                .forEach(u -> storage.bufferMessage(u, msg));
    }
}
