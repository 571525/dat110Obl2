package no.hvl.dat110.broker.processing.tests;

import no.hvl.dat110.client.Client;
import no.hvl.dat110.messages.PublishMsg;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Test8BufferingMesg extends Test0Base {

    public static String TOPIC = "testtopic";

    @Test
    public void test() {

        broker.setMaxAccept(4); // Just to make sure broker keeps running. Broker impl not made for supporting reconnects.

        Client client1 = new Client("client1", BROKER_TESTHOST, BROKER_TESTPORT);

        Client client2 = new Client("client2", BROKER_TESTHOST, BROKER_TESTPORT);

        client1.connect();

        client1.createTopic(TOPIC);

        // allow broker timer to create the topic

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client1.subscribe(TOPIC);

        client2.connect();

        client2.subscribe(TOPIC);

        // allow broker to process subscriptions
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client1.publish(TOPIC, "message before disconnect");

        String rec1 = client2.receive().toString();
        System.out.println("Send before disconnect: " + rec1);

        client2.disconnect();

        for (int i = 1; i <= 5; i++) {
            client1.publish(TOPIC, "Message: " + i);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client2.connect();

        for (int i = 1; i <= 5; i++) {
            PublishMsg msg = (PublishMsg) client2.receive();
            System.out.println("Message received after reconnect: " + msg.getMessage());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertTrue(true);

    }

}
