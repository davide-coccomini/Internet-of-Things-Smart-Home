package iot.unipi.cloudapp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;

public class Server extends CoapServer {

	private static JSONParser parser = new JSONParser();
	private static List<String> roomsTemperatureSensorsAvailable = new ArrayList<String>();
	private static List<String> outletSensorsAvailable = new ArrayList<String>();
	private static List<String> windowsActuatorsAvailable = new ArrayList<String>();
	private static Map<String,List<SensorValue>> sensorsValues = new HashMap<String,List<SensorValue>>();

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
	}
	
	public static void main(String[] args) {
		System.out.print("Running it!");
		Server server = new Server();
		server.add(new RegistrationResource("sensorRegistration"));
		server.start();
	}
}
