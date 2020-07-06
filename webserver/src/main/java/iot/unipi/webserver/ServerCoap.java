package iot.unipi.webserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.californium.core.CoapServer;

public class ServerCoap extends CoapServer{
    private static ServerCoap serverInstance = null;
    
    public static Map<String, Mote> motesList = new HashMap<String, Mote>();
    
    public static ServerCoap getCoapInstance(){
        if(serverInstance == null){
            System.out.println("######################Coap init######################");
            ServerCoap server = new ServerCoap();
            server.add(new RegistrationResource("sensorRegistration"));
            server.add(new MoteIPAddressResource("moteAddress"));
            server.start();
        }
        
        return serverInstance;
    }
    
}
