package iot.unipi.webserver;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class MoteIPAddressResource extends CoapResource {
	
    public MoteIPAddressResource(String name) {
            super(name);
    }
    
    public void handleGET(CoapExchange exchange) {
    	Response response = new Response(ResponseCode.CONTENT);
 		
 		String name = exchange.getQueryParameter("actuatorName");
 		String actuatorIP = ServerCoap.motesList.get(name).getMoteIP();
 		
 		response.setPayload(actuatorIP);
 		
		exchange.respond(response);
    }
}
