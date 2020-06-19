package iot.unipi.cloudapp;

import org.json.JSONObject;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
//import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {
	private static String[] moteNames = {"Kitchen", "Bedroom", "Bath", "Garage", "Living"};
	private static int count = 0;
	
    public RegistrationResource(String name) {
            super(name);
    }

    public void handlePOST(CoapExchange exchange) {
        byte[] request = exchange.getRequestPayload();

        String content = new String(request);
        JSONObject contentJson = null;
        
        contentJson = new JSONObject(content.toString());
        System.out.println("--Registration mote--");
        System.out.println(contentJson);
        
        if (contentJson != null && contentJson.has("MoteInfo")){
            JSONObject moteInfo = (JSONObject) contentJson.get("MoteInfo");
            String moteIP = (String) exchange.getSourceAddress().getHostAddress();
            System.out.println("--Mote IP--");
            System.out.println(moteIP);
            String moteType = (String) moteInfo.get("MoteType");
            String moteResource = (String) moteInfo.get("MoteResource");
            
            Server.freeMotes.add(new Mote(moteIP, moteType, moteResource));
            
            Response response = new Response(ResponseCode.CONTENT);
            response.setPayload("Registered");
            exchange.respond(response);
            
            //DA UTILIZZARE PER ASSEGNAMENTO MOTE A STANZA
            CoapClient client = new CoapClient("coap://[" + moteIP + "]/" + moteResource);
            //CoapResponse nameResponse = 
            client.post("name="+moteNames[count],MediaTypeRegistry.TEXT_PLAIN);
            Server.assignedMotes.put(moteNames[count], new Mote(moteIP, moteType, moteResource));
            count++;
            
            if(moteResource.equals("temperature")) {
            	if(Server.assignedMotes.get("Kitchen") != null) {
            		String actuatorIP = Server.assignedMotes.get("Kitchen").getMoteIP();
            		System.out.println("--Actuator IP--");
                    System.out.println(actuatorIP);
            		CoapClient act = new CoapClient("coap://[" + moteIP + "]/temperature");
            		act.post("actuator="+actuatorIP,MediaTypeRegistry.TEXT_PLAIN);
            	}
            }
        	coapClient(moteIP, moteResource);
    	}
        
    }

    public static void coapClient(String moteIP, String moteResource) {
        CoapClient client = new CoapClient("coap://[" + moteIP + "]/" + moteResource);
        //CoapObserveRelation relation = 
		client.observe(
            new CoapHandler() {
                public void onLoad(CoapResponse response) {
                    String content = response.getResponseText();
                    JSONObject contentJson = null;
                    
                    contentJson = new JSONObject(content.toString());
                    
                    System.out.println("--Measures--");
                    System.out.println(contentJson);
                    if (contentJson != null && contentJson.has("MoteValue")){
                        JSONObject moteInfo = (JSONObject) contentJson.get("MoteValue");
                        String moteName = (String) moteInfo.get("MoteName");
                        String value = (String) moteInfo.get("Value");
                        
                        Mote mote = Server.assignedMotes.get(moteName);
                        if(mote != null){
                            mote.getValues().add(new SensorValue(value));
                        }                       
                    }
                }

                public void onError() {
                    System.err.println("Failed");
                }
            }
        );
    }
}