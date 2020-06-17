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

static int max_pow = 1200;
static int min_pow = 0;
char s_pow[5];

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);
  
/*---------------------------------------------------------------------------*/

EVENT_RESOURCE(res_power,
         "title=\"Power: ?outlet=0..\" POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
		 res_get_handler,
         res_post_handler,
         res_put_handler,
         NULL,
         res_event_handler);

static void res_event_handler(void){
  	int pow = (rand() % (max_pow - min_pow + 1)) + min_pow; 
	sprintf(s_pow, "%d", pow);
	

    // Notify all the observers
    coap_notify_observers(&res_power);
}

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	const char *name = NULL;
  	if(coap_get_post_variable(request, "name", &name)) {
		char new_room[15];
		sprintf(new_room, "%s, ", name);
		strcpy(mote_name[0], new_room);
		name_assigned = true;
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

	char msg[200];
	strcpy(msg,"{\"MoteValue\":{\"MoteName\":\"");
	strcat(msg,mote_name[0]);
	strcat(msg,"\",\"Value\":\"");
	strcat(msg,s_pow);
	strcat(msg,"\"}}");
	length = sizeof(msg);
	memcpy(buffer, (uint8_t *)msg, length-1);

  	coap_set_header_content_format(response, TEXT_PLAIN);
  	coap_set_header_etag(response, (uint8_t *)&length, 1);
  	coap_set_payload(response, (uint8_t *)buffer, length);
}
