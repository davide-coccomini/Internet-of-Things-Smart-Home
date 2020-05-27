package iot.unipi.it;

import org.eclipse.californium.core.CoapServer;

public class Server extends CoapServer {

	public static void main(String[] args) {
		System.out.print("Running it!");
		Server server = new Server();
		//server.add(new EnergyResource("Energy"));
		server.add(new TemperatureResource("Temperature"));
		server.start();

	}

}
