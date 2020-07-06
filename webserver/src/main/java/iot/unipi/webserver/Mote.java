package iot.unipi.webserver;

import java.util.ArrayList;
import java.util.List;


public class Mote {
    private String moteIP;
    private String moteType;
    private String moteResource;
    private String moteName;
    private String associatedMoteName;
    private String status;

    
    private List<SensorValue> values = new ArrayList<SensorValue>();
/*
    public Mote(String moteIP, String moteType, String moteResource) {
        this.moteIP = moteIP;
        this.moteType = moteType;
        this.moteResource = moteResource;
    }
    */
    public Mote(String moteName, String moteType, String moteResource) {
        this.moteName = moteName;
        this.moteType = moteType;
        this.moteResource = moteResource;
            
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
    
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}