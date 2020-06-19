package iot.webtest;

import java.util.ArrayList;
import java.util.List;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mote {
    private String moteIP;
    private String moteType;
    private String moteResource;
    //private String moteRoom;
    private String moteName;
    private String associatedMoteName;
    
    private List<SensorValue> values = new ArrayList<SensorValue>();
/*
    public Mote(String moteIP, String moteType, String moteResource) {
        this.moteIP = moteIP;
        this.moteType = moteType;
        this.moteResource = moteResource;
    }
    */
    public Mote(String moteName, String moteType, String moteResource) {
        try {
            this.moteName = moteName;
            this.moteType = moteType;
            this.moteResource = moteResource;
            
            
            values.add(new SensorValue("35"));
            Thread.sleep(1000);
            values.add(new SensorValue("40"));
            Thread.sleep(1000);
            values.add(new SensorValue("45"));
            Thread.sleep(1000);
            values.add(new SensorValue("50"));
            Thread.sleep(1000);
            values.add(new SensorValue("45"));
            Thread.sleep(1000);
            values.add(new SensorValue("40"));
            Thread.sleep(1000);
            values.add(new SensorValue("35"));
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Mote.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getMoteIP() {
            return moteIP;
    }

    public void setMoteIP(String moteIP) {
            this.moteIP = moteIP;
    }

    public String getMoteType() {
            return moteType;
    }

    public void setMoteType(String moteType) {
            this.moteType = moteType;
    }

    public String getMoteResource() {
            return moteResource;
    }

    public void setMoteResource(String moteResource) {
            this.moteResource = moteResource;
    }

    public String getMoteName() {
            return moteName;
    }

    public void setMoteName(String moteName) {
            this.moteName = moteName;
    }

    public String getAssociatedMoteName() {
            return associatedMoteName;
    }

    public void setAssociatedMoteName(String associatedMoteName) {
            this.associatedMoteName = associatedMoteName;
    }

    public List<SensorValue> getValues() {
            return values;
    }

    public void setValues(List<SensorValue> values) {
            this.values = values;
    }
    
}

 class SensorValue {
	private Double value;
	private Timestamp timestamp;
        
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