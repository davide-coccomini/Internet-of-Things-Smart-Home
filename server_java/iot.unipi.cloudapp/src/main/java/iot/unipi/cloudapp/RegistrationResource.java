package iot.unipi.cloudapp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {
	
    public RegistrationResource(String name) {
            super(name);
    }

    public void handlePOST(CoapExchange exchange) {
        byte[] request = exchange.getRequestPayload();

        String content = new String(request);
        System.out.println(content);
        JSONObject contentJson = null;
        try {
                contentJson = (JSONObject) (new JSONParser()).parse(content);
        } catch (ParseException e) {
                e.printStackTrace();
        }
        System.out.println(contentJson);
        
        if (contentJson.containsKey("MoteInfo")){
            JSONObject moteInfo = (JSONObject) contentJson.get("MoteInfo");
            String moteIP = (String) exchange.getSourceAddress().getHostAddress();
            System.out.println(moteIP);
            String moteType = (String) moteInfo.get("MoteType");
            String moteResource = (String) moteInfo.get("MoteResource");
            
            Server.freeMotes.add(new Mote(moteIP, moteType, moteResource));
            
            Response response = new Response(ResponseCode.CONTENT);
            response.setPayload("Registered");
            exchange.respond(response);
            
            coapClient(moteIP, moteResource);
    	}
        
    }
    
    

    public static void coapClient(String moteIP, String moteResource) {
        CoapClient client = new CoapClient("coap://[" + moteIP + "]/" + moteResource + "?room=1");
        CoapObserveRelation relation = client.observe(
            new CoapHandler() {
                public void onLoad(CoapResponse response) {
                    String content = response.getResponseText().toString();
                    System.out.println(content);
                    try {
                        JSONObject contentJson = (JSONObject) (new JSONParser()).parse(content);
                        System.out.println(contentJson);
                        
                        if (contentJson.containsKey("MoteValue")){
                            JSONObject moteInfo = (JSONObject) contentJson.get("MoteValue");
                            String moteName = (String) moteInfo.get("MoteName");
                            String value = (String) moteInfo.get("Value");
                            
                            Mote mote = Server.assignedMotes.get(moteName);
                            if(mote != null){
                                mote.getValues().add(new SensorValue(value));
                            }
                                    
                            
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                public void onError() {
                    System.err.println("Failed");
                }
            }
        );
    }
}
