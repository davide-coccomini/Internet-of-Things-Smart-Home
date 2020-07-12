#include "contiki.h"
#include "coap-engine.h"
#include <string.h>
#include "dev/leds.h"
#include "../../global_conf.h"
#include "sys/log.h"


char mote_name[1][15];
bool name_assigned = false;

bool status_changed = false;
bool window_open = false;

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);
  
/*---------------------------------------------------------------------------*/

EVENT_RESOURCE(res_window,
         "title=\"Window: ?room=0..\" POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
		 res_get_handler,
         res_post_handler,
         res_put_handler,
         NULL,
         res_event_handler);

static void res_event_handler(void){
	status_changed = false;
    // Notify all the observers
    coap_notify_observers(&res_window);
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
	else if(coap_get_post_variable(request, "value", &name)) {
		size_t len = 0;
		const char *text = NULL;
		int command = 0;

		len = coap_get_post_variable(request, "value", &text);
		if(len > 0) {
			command = atoi(text);

			if(command == 1){
				if(!window_open){
					printf("Open window\n");
					window_open = true;
					status_changed = true;
				}
				else printf("Window already open\n");
			}
			else if(command == 0){
				if(window_open){
					printf("Close window\n");
					window_open = false;
					status_changed = true;	
				}
				else printf("Window already closed\n");
			}
			const char msg[] = "Command executed!";
			int length=sizeof(msg);
			coap_set_header_content_format(response, TEXT_PLAIN);
			coap_set_header_etag(response, (uint8_t *)&length, 1);
			coap_set_payload(response, (uint8_t *)msg, length);
			coap_set_status_code(response, CHANGED_2_04);
		}
	}
}

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
  	int length;

	char msg[200];
	strcpy(msg,"{\"MoteValue\":{\"MoteName\":\"");
	strcat(msg,mote_name[0]);
	strcat(msg,"\",\"Value\":\"");
	strcat(msg,window_open?"1":"0");
	strcat(msg,"\"}}");
	length = sizeof(msg);
	memcpy(buffer, (uint8_t *)msg, length-1);

  	coap_set_header_content_format(response, TEXT_PLAIN);
  	coap_set_header_etag(response, (uint8_t *)&length, 1);
  	coap_set_payload(response, (uint8_t *)buffer, length);
}
