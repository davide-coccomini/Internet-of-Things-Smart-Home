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
                    if(mote.getMoteType().equals("Actuator")){
                        obj.put("value", mote.getValues().get(0).getValue());
                    }
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
                client.put("name="+newName,MediaTypeRegistry.TEXT_PLAIN);

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
            
            if(moteName.equals("None") && ServerCoap.motesList.get(actuatorName) != null){
                Mote actuator = ServerCoap.motesList.get(actuatorName);
                String associatedMoteName = actuator.getAssociatedMoteName();
                if(associatedMoteName != null && ServerCoap.motesList.get(associatedMoteName) != null){
                    Mote mote = ServerCoap.motesList.get(associatedMoteName);
                    mote.setAssociatedMoteName(null);
                    actuator.setAssociatedMoteName(null);
                    CoapClient act = new CoapClient("coap://[" + mote.getMoteIP() + "]/temperature");
                    act.post("actuator=None",MediaTypeRegistry.TEXT_PLAIN);
                    
                    response.setHeader("Cache-Control", "no-cache");
                }
            }
            else if(ServerCoap.motesList.get(moteName) != null && ServerCoap.motesList.get(actuatorName) != null) {
                String moteIP = ServerCoap.motesList.get(moteName).getMoteIP();
                String actuatorIP = ServerCoap.motesList.get(actuatorName).getMoteIP();
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
        if (action.equals("actuatorValue")) {
            String actuatorName = request.getParameter("actuatorName");
            
            if(ServerCoap.motesList.get(actuatorName) != null) {
                Mote actuator = ServerCoap.motesList.get(actuatorName);
                String actuatorIP = actuator.getMoteIP();
                CoapClient act = new CoapClient("coap://[" + actuatorIP + "]/window");
                act.put("value="+(actuator.getValues().get(0).getValue()==0?1:0),MediaTypeRegistry.TEXT_PLAIN);
                
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