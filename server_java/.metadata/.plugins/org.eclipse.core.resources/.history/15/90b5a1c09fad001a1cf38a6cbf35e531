package iot.unipi.cloudapp;

import java.net.InetAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Client {
	private static JSONParser parser = new JSONParser();

	public static void coapClient(InetAddress inetAddress, String resource) {
		String ip = inetAddress.toString();
    	CoapClient client = new CoapClient("coap://" + ip + "/" + resource);
    	CoapObserveRelation relation = client.observe(
        	new CoapHandler() {
        
	        	public void onLoad(CoapResponse response) {
	        		String content = response.getResponseText();
	            		System.out.println(content);
	            		try {
							JSONObject contentJson = (JSONObject) parser.parse(content);
		            		System.out.println(contentJson);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		//SALVARE IL VALORE DEL SENSORE IN UN ARRAY
	            		//SensorValue sensorValue = new SensorValue(contentJson);
        		}
	        	
	        	public void onError() {
	            		System.err.println("Failed");
	        	}
        	}
    	);
		
		System.out.print("PIPPO\n");
		
		CoapClient client = new CoapClient("coap://127.0.0.1:5683/hello");
				
		CoapResponse response = client.get();

		System.out.print(response.getResponseText()+"\n");
			
		response = client.post("10", MediaTypeRegistry.TEXT_PLAIN);
		
		System.out.print(response.getResponseText()+"\n");
		
		Request request = new Request(CoAP.Code.GET);
		
		request.setOptions((new OptionSet()).setAccept(MediaTypeRegistry.APPLICATION_JSON));
		
		response = client.advanced(request);
		
		System.out.print(response.getResponseText()+"\n");
		
		try {
			JSONObject genreJsonObject = (JSONObject) JSONValue.parseWithException(response.getResponseText());
		
			System.out.println(genreJsonObject.get("value"));
					
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}

	}

}
