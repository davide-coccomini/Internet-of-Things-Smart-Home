## Smart-Home
This project has been designed with the aim of creating a smart-home in which the energy consumed by the various appliances is constantly controlled as well as the temperature which, depending on the heat inside the house, opens or closes the windows automatically and independently.

In order to be able to monitor the values recorded by the sensors, a web interface is also available that allows you to change the sensor settings and send commands directly to the actuators. The interface is provided by a Web Server installed in the cloud and to which the sensors must register.

The Cooja simulator was used to simulate the smart-home environment and to test the sensors and machine-to-machine interaction between them.

### Sensors
The mote that have been defined are 3: 
* **Power sensor**: installed on an electrical outlet to which an appliance is attached in order to constantly monitor the energy consumed.
* **Temperature sensor**: installed in several rooms of the house to monitor the temperature recorded.
* **Window actuator**: actuator able to execute the commands sent by the temperature sensor, depending on the recorded heat it opens or closes the window.

The resources associated with the sensors implement the 3 main request methods:
* GET, to collect the values recorded by the sensor;
* POST, to set values in the sensor, such as name or associated actuator IP.
* PUT, to modify later values set in the POST.

In order for the sensors to send the recorded data to the Web Server, it is also necessary to install a **Border Router** to which all the sensors that want to communicate with the outside must connect.

### Architecture
The CoAP protocol is of course used to communicate with the outside world.
Every mote developed is both CoAP client and CoAP server, the client part only serves to register the sensor in the CoAP Server implemented in the Web Server: to register the sensor sends a request to the server address specifying the resource "*/sensorRegistration*" that takes care of saving the IP address of the sensor, the type and the resource it manages; instead the CoAP server part is used to activate the resource and manage the observers who want to register calling the *coap_notify_observers(&res)* command.

Once the sensor has been registered, the Web Server creates a CoAP observer that registers to the resource managed by the sensor so that it continuously receives the recorded values every time a change is detected using the *res.trigger()* command. 

This behavior just described is shown in the following picture taking as an example the interaction between the Web Server and the temperature sensor:
<p align="center">
  <img src="https://github.com/davide-coccomini/smart-home/blob/master/overview.png">
</p>
