#include "contiki.h"
#include "net/routing/routing.h"
#include "random.h"
#include "net/netstack.h"
#include "net/ipv6/simple-udp.h"
#include "node-id.h"
#include "../global_conf.h"

#include "coap-engine.h"
#include "coap-blocking-api.h"

#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_INFO

#define UDP_CLIENT_PORT	8765
#define UDP_SERVER_PORT	5678

#define SERVER_EP "coap://[fd00::1]:5683"
#define SERVER_REGISTRATION "/sensorRegistration"

#define TOGGLE_INTERVAL 10

static struct etimer et;
static struct etimer e_timer;

bool registered = false;

extern coap_resource_t res_window;

/*---------------------------------------------------------------------------*/
PROCESS(coap_client, "CoAP Client");
PROCESS(coap_server, "CoAP Server");
AUTOSTART_PROCESSES(&coap_client, &coap_server);

/*---------------------------------------------------------------------------*/
void response_handler(coap_message_t *response){
	const uint8_t *chunk;
	if(response == NULL) {
		puts("Request timed out");
		return;
	}
	int len = coap_get_payload(response, &chunk);
	printf("|%.*s\n", len, (char *)chunk);
}

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(coap_client, ev, data){

	static coap_endpoint_t server_ep;
	static coap_message_t request[1];
	uip_ipaddr_t dest_ipaddr;

	PROCESS_BEGIN();
	
	coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

	etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);

	while(1) {

		printf("Waiting connection..\n");
		PROCESS_YIELD();

		if((ev == PROCESS_EVENT_TIMER && data == &et) || ev == PROCESS_EVENT_POLL) {
			
			if(NETSTACK_ROUTING.node_is_reachable() && NETSTACK_ROUTING.get_root_ipaddr(&dest_ipaddr)){	
				printf("--Registration--\n");

				coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
				coap_set_header_uri_path(request, (const char *)&SERVER_REGISTRATION);
				const char msg[] = "{\"MoteInfo\":{\"MoteType\":\"Actuator\",\"MoteResource\":\"window\"}}";
				printf("%s\n", msg);
				coap_set_payload(request, (uint8_t *)msg, sizeof(msg)-1);

				COAP_BLOCKING_REQUEST(&server_ep, request, response_handler);
				registered = true;
				break;
			}
			else{
				printf("Not rpl address yet\n");
			}

			etimer_reset(&et);
		}
	}

	PROCESS_END();
}

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(coap_server, ev, data){

	PROCESS_BEGIN();

	printf("Starting Window Actuator\n");
	coap_activate_resource(&res_window, "window");
	
	etimer_set(&e_timer, CLOCK_SECOND * 10);

	printf("Get window values\n");

	while(1) {
		
		PROCESS_WAIT_EVENT();
	
		if(ev == PROCESS_EVENT_TIMER && data == &e_timer){
			if(registered && name_assigned && status_changed){
				res_window.trigger();
				status_changed = false;
			}

			etimer_set(&e_timer, CLOCK_SECOND * 5);
		}
	}

	PROCESS_END();
}
