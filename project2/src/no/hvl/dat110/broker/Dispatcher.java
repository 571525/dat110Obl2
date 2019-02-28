package no.hvl.dat110.broker;

import no.hvl.dat110.common.Logger;
import no.hvl.dat110.common.Stopable;
import no.hvl.dat110.messages.*;
import no.hvl.dat110.messagetransport.Connection;

import java.util.Collection;

public class Dispatcher extends Stopable {

    private Storage storage;

    public Dispatcher(Storage storage) {
        super("Dispatcher");
        this.storage = storage;

    }

    @Override
    public void doProcess() {

        Collection<ClientSession> clients = storage.getSessions();

        Logger.lg(".");
        for (ClientSession client : clients) {

            Message msg = null;

            if (client.hasData()) {
                msg = client.receive();
            }

            if (msg != null) {
                dispatch(client, msg);
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    }

    // called by dispatch upon receiving a disconnect message
    public void onDisconnect(DisconnectMsg msg) {

        String user = msg.getUser();

        Logger.log("onDisconnect:" + msg.toString());

        storage.removeClientSession(user);

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
        storage.getSubscribers(msg.getTopic()).stream().forEach(a -> storage.getSession(a).send(msg));

    }
}
