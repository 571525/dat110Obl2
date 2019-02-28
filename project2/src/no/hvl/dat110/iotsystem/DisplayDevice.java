package no.hvl.dat110.iotsystem;

import no.hvl.dat110.client.Client;
import no.hvl.dat110.messages.PublishMsg;

public class DisplayDevice {

    private static final int COUNT = 10;

    public static void main(String[] args) {

        System.out.println("Display starting ...");

        // TODO - START
        Client dispdevice = new Client("DispDevice", Common.BROKERHOST, Common.BROKERPORT);
        dispdevice.connect();
        dispdevice.createTopic(Common.TEMPTOPIC);
        dispdevice.subscribe(Common.TEMPTOPIC);

        int i = 0;
        while (i < COUNT) {
            PublishMsg msg = (PublishMsg) dispdevice.receive();
            if (msg != null) {
                System.out.println("Disp Device: " + msg.getMessage());
                i++;
            }
        }

        dispdevice.disconnect();
        // TODO - END

        System.out.println("Display stopping ... ");

    }
}
