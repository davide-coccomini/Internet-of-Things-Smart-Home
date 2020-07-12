#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include "dev/leds.h"
#include "../../global_conf.h"
#include "sys/log.h"

char mote_name[1][15];
bool name_assigned = false;

static int max_temp = 50;
static int min_temp = 0;
int temp;
char s_temp[3] = "0";

char actuator_ip[39];
bool actuator_ip_assigned = false;

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);
  
/*---------------------------------------------------------------------------*/

EVENT_RESOURCE(res_temperature,
         "title=\"Temperature: ..\" POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
		 res_get_handler,
         res_post_handler,
         res_put_handler,
         NULL,
         res_event_handler);

static void res_event_handler(void){
	temp = (rand() % (max_temp - min_temp + 1)) + min_temp;
	sprintf(s_temp, "%d", temp);
	 // Notify all the observers
    coap_notify_observers(&res_temperature);
}

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	const char *name = NULL;
  	if(coap_get_post_variable(request, "name", &name)) {
		char new_room[15] = "";
		sprintf(new_room, "%s", name);
		strcpy(mote_name[0], new_room);
		printf("Name received: %s\n", mote_name[0]);
		name_assigned = true;
		coap_set_status_code(response, CREATED_2_01);
	}
	else if(coap_get_post_variable(request, "actuator", &name)) {
		char ip[39] = "";
		sprintf(ip, "%s", name);
		printf("Actuator IP associated: %s\n", ip);
		int x;
		if((x = strcmp(ip, "None"))==0){
			strcpy(actuator_ip, "");
			printf("None\n");
			actuator_ip_assigned = false;
			coap_set_status_code(response, CREATED_2_01);
		}
		else{
			printf("Else\n");
			strcpy(actuator_ip, "coap://[");
			strcat(actuator_ip,ip);
			strcat(actuator_ip,"]:5683");
			actuator_ip_assigned = true;
			coap_set_status_code(response, CREATED_2_01);
		}
  	}else{
	  	coap_set_status_code(response, BAD_REQUEST_4_00);
  	}
}

static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	const char *name = NULL;
  	if(coap_get_post_variable(request, "name", &name)) {
		char new_room[15] = "";
		sprintf(new_room, "%s", name);
		strcpy(mote_name[0], new_room);
		printf("Name received: %s\n", mote_name[0]);
		name_assigned = true;
		coap_set_status_code(response, CREATED_2_01);
		#if PLATFORM_HAS_LEDS || LEDS_COUNT
			leds_on(LEDS_NUM_TO_MASK(LEDS_GREEN));
		#endif
	}
	else if(coap_get_post_variable(request, "actuator", &name)) {
		char ip[39] = "";
		sprintf(ip, "%s", name);
		printf("Actuator IP associated: %s\n", ip);
		int x;
		if((x = strcmp(ip, "None"))==0){
			strcpy(actuator_ip, "");
			printf("None\n");
			actuator_ip_assigned = false;
			coap_set_status_code(response, CREATED_2_01);
		}
		else{
			printf("Else\n");
			strcpy(actuator_ip, "coap://[");
			strcat(actuator_ip,ip);
			strcat(actuator_ip,"]:5683");
			actuator_ip_assigned = true;
			coap_set_status_code(response, CREATED_2_01);
		}
  	}else{
	  	coap_set_status_code(response, BAD_REQUEST_4_00);
  	}
}

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
  	int length;

	char msg[300];
	strcpy(msg,"{\"MoteValue\":{\"MoteName\":\"");
	strcat(msg,mote_name[0]);
	strcat(msg,"\",\"Value\":\"");
	strcat(msg,s_temp);
	strcat(msg,"\"}}\0");

	length = sizeof(msg);
	memcpy(buffer, (uint8_t *)msg, length-1);

  	coap_set_header_content_format(response, TEXT_PLAIN);
  	coap_set_header_etag(response, (uint8_t *)&length, 1);
  	coap_set_payload(response, (uint8_t *)buffer, length);
}
