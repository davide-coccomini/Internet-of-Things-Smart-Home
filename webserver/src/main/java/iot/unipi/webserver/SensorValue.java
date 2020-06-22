package iot.unipi.webserver;

import java.sql.Timestamp;

public class SensorValue {
	Double value;
	Timestamp timestamp;
        
        public SensorValue(String value){
            if(value.equals("open")){
                this.value = 1.0;
            }
            else if(value.equals("close")){
                this.value = 0.0;
            }
            else{
                this.value = Double.parseDouble(value);
            }
            timestamp = new Timestamp(System.currentTimeMillis());
        }
	
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
		
}
