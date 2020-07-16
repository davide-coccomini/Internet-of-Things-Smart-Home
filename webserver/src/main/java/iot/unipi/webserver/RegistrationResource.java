package iot.unipi.webserver;

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
            String moteType = (String) moteInfo.get("MoteType");
            String moteResource = (String) moteInfo.get("MoteResource");
            
            
            Response response = new Response(ResponseCode.CONTENT);
            response.setPayload("Registered");
            exchange.respond(response);
            
            String moteName = "Mote" + count++;
            CoapClient client = new CoapClient("coap://[" + moteIP + "]/" + moteResource);
            client.post("name="+moteName,MediaTypeRegistry.TEXT_PLAIN);
            Mote newMote = new Mote(moteName, moteType, moteResource);
            newMote.setMoteIP(moteIP);
            ServerCoap.motesList.put(moteName, newMote);
            
            coapClient(moteIP, moteResource);
            
    	}
        
    }

    public static void coapClient(String moteIP, String moteResource) {
        CoapClient client = new CoapClient("coap://[" + moteIP + "]/" + moteResource);
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
                        
                        Mote mote = ServerCoap.motesList.get(moteName.split(",")[0]);
                        if(mote != null){
                            if(mote.getMoteType().equals("Sensor")){
                               mote.getValues().add(new SensorValue(value));
                            }
                            if(mote.getMoteType().equals("Actuator")){
                                mote.getValues().clear();
                                mote.getValues().add(new SensorValue(value));
                            }
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
