package iot.unipi.cloudapp;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;

public class Server {

	public static void main(String[] args) {
		CoapClient client = new CoapClient("coap://127.0.0.1/Temperature");
		CoapObserveRelation relation = client.observe(
				new CoapHandler() {
				
					public void onLoad(CoapResponse response) {
						String content = response.getResponseText();
						System.out.println(content);
					}
					
					public void onError() {
						System.err.println("Failed");
					}
				});
		try {
			Thread.sleep (6*1000); 
		} 
		catch (InterruptedException e) { }
		
		relation.proactiveCancel();
	}
	

}