package iot.unipi.webserver;

import java.sql.Timestamp;

public class SensorValue {
	Double value;
	Timestamp timestamp;
        
        public SensorValue(String value){
            this.value = Double.parseDouble(value);
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
