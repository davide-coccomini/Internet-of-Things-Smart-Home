#include "contiki.h"
#include "net/routing/routing.h"
#include "net/netstack.h"
#include "net/ipv6/simple-udp.h"


#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_INFO

#define UDP_CLIENT_PORT	8765
#define UDP_SERVER_PORT	5678

static struct simple_udp_connection udp_conn;

PROCESS(udp_server_process, "UDP server");
AUTOSTART_PROCESSES(&udp_server_process);
/*---------------------------------------------------------------------------*/
static void
udp_rx_callback(struct simple_udp_connection *c, const uip_ipaddr_t *sender_addr, uint16_t sender_port, const uip_ipaddr_t *receiver_addr, uint16_t receiver_port, const uint8_t *data, uint16_t datalen){
	LOG_INFO("%s ", data);
	LOG_INFO_6ADDR(sender_addr);
	LOG_INFO_("\n");
}
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(udp_server_process, ev, data){
	PROCESS_BEGIN();

	#if BORDER_ROUTER_CONF_WEBSERVER
		PROCESS_NAME(webserver_nogui_process);
		process_start(&webserver_nogui_process, NULL);
	#endif 

	simple_udp_register(&udp_conn, UDP_SERVER_PORT, NULL, UDP_CLIENT_PORT, udp_rx_callback);
	
	PROCESS_END();
}
