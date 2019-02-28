package no.hvl.dat110.iotsystem;

import no.hvl.dat110.broker.Broker;
import no.hvl.dat110.broker.ClientSession;
import no.hvl.dat110.broker.Dispatcher;
import no.hvl.dat110.broker.Storage;
import no.hvl.dat110.client.Client;
import no.hvl.dat110.messages.PublishMsg;
import no.hvl.dat110.messagetransport.Connection;

import java.net.Socket;

public class TemperatureDevice {
	
	private static final int COUNT = 10;
	
	public static void main(String[] args) {
		
		TemperatureSensor sn = new TemperatureSensor();
		
		// TODO - start
		Client tempdevice = new Client("TempDevice",Common.BROKERHOST,Common.BROKERPORT);
		tempdevice.connect();

		for (int i = 0; i< COUNT; i++) {
			int temp = sn.read();
			tempdevice.publish(Common.TEMPTOPIC,"Temp: " + temp);

			//wait a bit before reading again
			try{
				Thread.sleep(1000);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		tempdevice.disconnect();
		// TODO - end

		System.out.println("Temperature device stopping ... ");
	}
}
