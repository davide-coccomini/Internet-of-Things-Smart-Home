package iot.unipi.it;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import java.util.Random;

public class EnergyResource extends CoapResource {

	public EnergyResource(String name) {
		super(name);
		setObservable(true);
 	}
 	public void handleGET(CoapExchange exchange) {
 		
 		Response response = new Response(ResponseCode.CONTENT);
 		Random rand = new Random();
 		int n = rand.nextInt(40);
 		
 		if( exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_XML ) {
 			response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_XML);
 			response.setPayload("<value>" + n + "</value>");
 		} else if(exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
 			response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
 			response.setPayload("{\"value\":\""+ n + "\"}"); 			
 		} else {
 			response.setPayload("Energy Consumpion: "+ n);
 		}
 		
		exchange.respond(response);
 	}

}
