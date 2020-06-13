package iot.unipi.cloudapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RegistrationResource extends CoapResource {

	private static List<String> roomsTemperatureSensorsAvailable = new ArrayList<String>();
	private static List<String> outletSensorsAvailable = new ArrayList<String>();
	private static List<String> windowsActuatorsAvailable = new ArrayList<String>();
	private static Map<String,List<SensorValue>> sensorsValues = new HashMap<String,List<SensorValue>>();
	
	public RegistrationResource(String name) {
		super(name);
	}

	public void handlePOST(CoapExchange exchange) {
		
		byte[] request = exchange.getRequestPayload();
		
		String content = new String(request);
		JSONObject contentJson = null;
		try {
			contentJson = (JSONObject) (new JSONParser()).parse(content);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(contentJson);
				
		if (contentJson.containsKey("MoteInfo")){
    		JSONObject moteInfo = (JSONObject) contentJson.get("MoteInfo");
        	String moteType = (String) moteInfo.get("MoteType");
        	String moteResource = (String) moteInfo.get("MoteResource");
        	
        	Response response = new Response(ResponseCode.CONTENT);
    		response.setPayload(moteResource);
    		exchange.respond(response);
    	}
		
 	}
}
