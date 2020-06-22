#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include "../../global_conf.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

char mote_name[1][15];
//static int actual_rooms = 5;
//static int max_rooms = 7;

//char *mote_name;
bool name_assigned = false;

static int max_temp = 50;
static int min_temp = 0;
int temp;
char s_temp[3];

char actuator_ip[39];
bool actuator_ip_assigned = false;

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);
  
/*---------------------------------------------------------------------------*/

EVENT_RESOURCE(res_temperature,
         "title=\"Temperature: ?room=0..\" POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
		 res_get_handler,
         res_post_handler,
         res_put_handler,
         NULL,
         res_event_handler);

static void res_event_handler(void){
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
		strcpy(actuator_ip, "coap://[");
		strcat(actuator_ip,ip);
		strcat(actuator_ip,"]:5683");
		actuator_ip_assigned = true;
		coap_set_status_code(response, CREATED_2_01);
  	}else{
	  	coap_set_status_code(response, BAD_REQUEST_4_00);
  	}
}

static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	/*size_t len = 0;
	const char *text = NULL;
	char room[15];
		memset(room, 0, 15);
	char temp[32];
		memset(temp, 0, 32);
	int success_1 = 0;
	int success_2 = 0;

	len = coap_get_post_variable(request, "name", &text);
	if(len > 0 && len < 15) {
		memcpy(room, text, len);
		success_1 = 1;
	}

	len = coap_get_post_variable(request, "value", &text);
	if(len > 0 && len < 32 && success_1 == 1) {
		memcpy(temp, text, len);
		char msg[50];
		memset(msg, 0, 50);
		sprintf(msg, "Temp in %s set to %s", room, temp);
		int length=sizeof(msg);
		coap_set_header_content_format(response, TEXT_PLAIN);
		coap_set_header_etag(response, (uint8_t *)&length, 1);
		coap_set_payload(response, msg, length);
		success_2=1;
		coap_set_status_code(response, CHANGED_2_04);
	}
	if (success_2 == 0){
		coap_set_status_code(response, BAD_REQUEST_4_00);
	}*/
}

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
  	int length;
	
	temp = (rand() % (max_temp - min_temp + 1)) + min_temp;
	sprintf(s_temp, "%d", temp);

	char msg[200];
	strcpy(msg,"{\"MoteValue\":{\"MoteName\":\"");
	strcat(msg,mote_name[0]);
	strcat(msg,"\",\"Value\":\"");
	strcat(msg,s_temp);
	strcat(msg,"\"}}");
	length = sizeof(msg);
	memcpy(buffer, (uint8_t *)msg, length-1);

  	coap_set_header_content_format(response, TEXT_PLAIN);
  	coap_set_header_etag(response, (uint8_t *)&length, 1);
  	coap_set_payload(response, (uint8_t *)buffer, length);
}
