#include "contiki.h"
#include "net/routing/routing.h"
#include "random.h"
#include "net/netstack.h"
#include "net/ipv6/simple-udp.h"
#include "node-id.h"

#include "coap-engine.h"
#include "coap-blocking-api.h"

#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_INFO

#define UDP_CLIENT_PORT	8765
#define UDP_SERVER_PORT	5678

#define SERVER_EP "coap://[fd00::203:3:3:3]:5683"
char *service_url = "/hello";
#define TOGGLE_INTERVAL 10

static struct simple_udp_connection udp_conn;
static struct etimer et;
//#define START_INTERVAL		(5 * CLOCK_SECOND)

bool rpl_add = false;

/*---------------------------------------------------------------------------*/
PROCESS(udp_client_process, "UDP client");
PROCESS(er_example_client, "Erbium Example Client");
AUTOSTART_PROCESSES(&udp_client_process, &er_example_client);

/*---------------------------------------------------------------------------*/
static void udp_rx_callback(struct simple_udp_connection *c, const uip_ipaddr_t *sender_addr,
         uint16_t sender_port, const uip_ipaddr_t *receiver_addr, uint16_t receiver_port, const uint8_t *data, uint16_t datalen){
  LOG_INFO("Received response %s ", data);
  LOG_INFO_6ADDR(sender_addr);
  LOG_INFO_("\n");
}

void
client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;

  if(response == NULL) {
    puts("Request timed out");
    return;
  }

  int len = coap_get_payload(response, &chunk);

  printf("|%.*s", len, (char *)chunk);
}

/*---------------------------------------------------------------------------*/
PROCESS_THREAD(udp_client_process, ev, data){
  static struct etimer periodic_timer;
  uip_ipaddr_t dest_ipaddr;
  static unsigned int message_number = 0;
  
  int START_INTERVAL = 5;

  PROCESS_BEGIN();
  /* Initialize UDP connection */
  simple_udp_register(&udp_conn, UDP_CLIENT_PORT, NULL, UDP_SERVER_PORT, udp_rx_callback);
  etimer_set(&periodic_timer, START_INTERVAL);
  
  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&periodic_timer));
    if(NETSTACK_ROUTING.node_is_reachable() && NETSTACK_ROUTING.get_root_ipaddr(&dest_ipaddr)) {
      /* Send to DAG root */
      LOG_INFO("Sending request %u to ", message_number);
      LOG_INFO_6ADDR(&dest_ipaddr);
      LOG_INFO_("\n");
      char buf[300];
      sprintf(buf, "Message %d from node %d", message_number, node_id);
      message_number++;
      simple_udp_sendto(&udp_conn, buf, strlen(buf) + 1, &dest_ipaddr);
	  rpl_add = true;
	  //printf(rpl_add + "\n");
	  //etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);
	  
	  START_INTERVAL = 50;
    } 
    else {
      LOG_INFO("Not reachable yet\n");
    }
    etimer_set(&periodic_timer, START_INTERVAL * CLOCK_SECOND);
  }
  
  PROCESS_END();
}
  
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(er_example_client, ev, data){

  static coap_endpoint_t server_ep;
  static coap_message_t request[1]; 
  
  PROCESS_BEGIN();
  /* COAP client */
  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

  etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);

  while(1) {
  
  	printf("while coap\n");
  	//PROCESS_WAIT_UNTIL(rpl_add);
  	PROCESS_YIELD();
  	printf("timer wait\n");
  	if((ev == PROCESS_EVENT_TIMER && data == &et) || 
	      ev == PROCESS_EVENT_POLL) {
	      
      if(rpl_add == true){
	
		  printf("--Toggle timer--\n");

		  /* prepare request, TID is set by COAP_BLOCKING_REQUEST() */
		  coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
		  coap_set_header_uri_path(request, service_url);

		  const char msg[] = "Toggle!";

		  coap_set_payload(request, (uint8_t *)msg, sizeof(msg) - 1);

		  COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

		  printf("\n--Done--\n");
	  }
	  
	  else{
	  	printf("no rpl add\n");
  	 }

	  etimer_reset(&et);
	}
	
  }
  
  PROCESS_END();
}
