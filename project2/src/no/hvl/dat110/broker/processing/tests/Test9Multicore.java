package no.hvl.dat110.broker.processing.tests;

import no.hvl.dat110.client.Client;
import no.hvl.dat110.messages.PublishMsg;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class Test9Multicore extends Test0Base {

    public static String TOPIC = "testtopic";

    @Test
    public void test() {

        broker.setMaxAccept(6);

        Client client1 = new Client("client1", BROKER_TESTHOST, BROKER_TESTPORT);
        Client client2 = new Client("client2", BROKER_TESTHOST, BROKER_TESTPORT);
        Client client3 = new Client("client3", BROKER_TESTHOST, BROKER_TESTPORT);
        Client client4 = new Client("client4", BROKER_TESTHOST, BROKER_TESTPORT);
        Client client5 = new Client("client5", BROKER_TESTHOST, BROKER_TESTPORT);
        Client client6 = new Client("client6", BROKER_TESTHOST, BROKER_TESTPORT);

        client1.connect();
        client2.connect();
        client3.connect();
        client4.connect();
        client5.connect();
        client6.connect();

        client1.disconnect();
        client2.disconnect();
        client3.disconnect();
        client4.disconnect();
        client5.disconnect();
        client6.disconnect();

        assertTrue(true);

    }

}
