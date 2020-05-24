package iot.unipi.it;

import java.util.Random;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.Timer;
import java.util.TimerTask;

public class TemperatureResource extends CoapResource {

	public TemperatureResource(String name) {
		super(name);
		setObservable(true);
		getAttributes().setObservable(); // mark observable in the Link-Format
		
		// schedule a periodic update task, otherwise let events call changed()
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 1000);
 	}
	
	private class UpdateTask extends TimerTask {
		@Override
		public void run() {
			// .. periodic update of the resource
			changed(); // notify all observers
		}
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
 			response.setPayload("Temperature: " + n);
 		}
 		
		exchange.respond(response);
 	}
}
