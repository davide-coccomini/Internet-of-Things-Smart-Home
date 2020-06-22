package iot.webtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.californium.core.CoapServer;

public class ServerCoap extends CoapServer{
    public static Map<String, Mote> assignedMotes = new HashMap<String, Mote>();
    public static List<Mote> freeMotes = new ArrayList<Mote>();
    
    static{
        ServerCoap server = new ServerCoap();
        server.add(new RegistrationResource("sensorRegistration"));
        server.add(new MoteIPAddressResource("moteAddress"));
        server.start();
    }
}
