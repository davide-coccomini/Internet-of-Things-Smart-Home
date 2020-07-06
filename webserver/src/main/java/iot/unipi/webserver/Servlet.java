package iot.unipi.webserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONArray;
import org.json.JSONObject;


public class Servlet extends HttpServlet {
    private ServletContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.context = config.getServletContext();
        ServerCoap.getCoapInstance();
        /*
        assignedMotes.put("mote1", new Mote("mote1", "sensor", "temperature"));
        assignedMotes.put("mote2", new Mote("mote2", "sensor", "power"));
        assignedMotes.put("mote3", new Mote("mote3", "actuator", "window"));
        assignedMotes.put("mote4", new Mote("mote4", "sensor", "temperature"));
        */
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //System.out.println("LIST: " + ServerCoap.motesList);
        
        String action = request.getParameter("action");
        StringBuffer sb = new StringBuffer();

        if (action.equals("values")) {
            String name = request.getParameter("name");
            if(ServerCoap.motesList.containsKey(name)){
                Mote mote = ServerCoap.motesList.get(name);

                Iterator it = mote.getValues().iterator();

                JSONObject msg = new JSONObject();
                JSONArray values = new JSONArray();
                JSONArray labels = new JSONArray();

                while (it.hasNext()) {
                    SensorValue sensorValue = (SensorValue) it.next();

                    values.put(sensorValue.getValue());
                    labels.put(sensorValue.getTimestamp());
                }

                msg.put("values", values);
                msg.put("labels", labels);
                sb.append(msg.toString());

                response.setContentType("text/json");
                response.setHeader("Cache-Control", "no-cache");
                response.getWriter().write(sb.toString());
            }
            else {
                //nothing to show
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }
        if (action.equals("list")) {
            
            String type = request.getParameter("type");

            Iterator it = ServerCoap.motesList.keySet().iterator();

            JSONObject msg = new JSONObject();
            JSONArray array = new JSONArray();
            while (it.hasNext()) {
                String id = (String) it.next();
                Mote mote = (Mote) ServerCoap.motesList.get(id);
                
                if (mote.getMoteType().toLowerCase().equals(type.toLowerCase())) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", mote.getMoteName());
                    obj.put("resource", mote.getMoteResource());
                    obj.put("assigned", mote.getAssociatedMoteName());
                    array.put(obj);
                }
                
            }
            
            msg.put("array", array);
            sb.append(msg.toString());

            response.setContentType("text/json");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write(sb.toString());
        }
    }
    
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String action = request.getParameter("action");
        StringBuffer sb = new StringBuffer();
        
        if (action.equals("updateMote")) {
            String oldName = request.getParameter("oldName");
            String newName = request.getParameter("newName");
            if(ServerCoap.motesList.containsKey(oldName)){
                Mote mote = ServerCoap.motesList.remove(oldName);
                String moteIP = mote.getMoteIP();
                String moteResource = mote.getMoteResource();
                
                CoapClient client = new CoapClient("coap://[" + moteIP + "]/" + moteResource);
                client.post("name="+newName,MediaTypeRegistry.TEXT_PLAIN);

                mote.setMoteName(newName);
                
                if(mote.getAssociatedMoteName() != null){
                    Mote associatedMote = ServerCoap.motesList.get(mote.getAssociatedMoteName());
                    associatedMote.setAssociatedMoteName(newName);
                }
                
                ServerCoap.motesList.put(newName, mote);
                
                response.setHeader("Cache-Control", "no-cache");
            }
            else {
                //nothing to show
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }
        if (action.equals("assignActuator")) {
            String moteName = request.getParameter("moteName");
            String actuatorName = request.getParameter("actuatorName");
            
            if(ServerCoap.motesList.get(moteName) != null && ServerCoap.motesList.get(actuatorName) != null) {
                String moteIP = ServerCoap.motesList.get(moteName).getMoteIP();
                String actuatorIP = ServerCoap.motesList.get(actuatorName).getMoteIP();
                System.out.println("--Actuator IP--");
                System.out.println(actuatorIP);
                CoapClient act = new CoapClient("coap://[" + moteIP + "]/temperature");
                act.post("actuator="+actuatorIP,MediaTypeRegistry.TEXT_PLAIN);
                
                ServerCoap.motesList.get(moteName).setAssociatedMoteName(actuatorName);
                ServerCoap.motesList.get(actuatorName).setAssociatedMoteName(moteName);
                
                response.setHeader("Cache-Control", "no-cache");
            }
            else {
                //nothing to show
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }
        if (action.equals("actuatorStatus")) {
            String actuatorName = request.getParameter("actuatorName");
            
            if(ServerCoap.motesList.get(actuatorName) != null) {
                String moteIP = ServerCoap.motesList.get(moteName).getMoteIP();
                String actuatorIP = ServerCoap.motesList.get(actuatorName).getMoteIP();
                System.out.println("--Actuator IP--");
                System.out.println(actuatorIP);
                CoapClient act = new CoapClient("coap://[" + moteIP + "]/temperature");
                act.post("actuator="+actuatorIP,MediaTypeRegistry.TEXT_PLAIN);
                
                ServerCoap.motesList.get(moteName).setAssociatedMoteName(actuatorName);
                ServerCoap.motesList.get(actuatorName).setAssociatedMoteName(moteName);
                
                response.setHeader("Cache-Control", "no-cache");
            }
            else {
                //nothing to show
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}