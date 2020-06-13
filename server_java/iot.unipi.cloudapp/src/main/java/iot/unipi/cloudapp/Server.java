package iot.unipi.cloudapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.CoapServer;

public class Server extends CoapServer {
    public static Map<String, Mote> assignedMotes = new HashMap<String, Mote>();
    public static List<Mote> freeMotes = new ArrayList<Mote>();

	public static void main(String[] args) {
		System.out.print("Running it!");
        Server server = new Server();
        server.add(new RegistrationResource("sensorRegistration"));
        server.start();
	}

}
