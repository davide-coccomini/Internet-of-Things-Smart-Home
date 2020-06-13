#include "contiki.h"
#include "coap-engine.h"
#include <string.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP
static char rooms_avl[6][15] = {
        "kitchen, ",
        "bedroom 1, ",
        "bedroom 2, ",
        "hall, ",
        "living room ",
    };
static int actual_rooms = 5;
static int max_rooms = 7;
static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/* A simple actuator example, depending on the color query parameter and post variable mode, corresponding led is activated or deactivated */
RESOURCE(res_temperature,
         "title=\"Temperature: ?room=0..\" POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
		 res_get_handler,
         res_post_handler,
         res_put_handler,
         NULL);

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
  const char *name = NULL;
  if(coap_get_post_variable(request, "name", &name)&& actual_rooms <= max_rooms) {
    char new_room[15];
    sprintf(new_room, "%s, ", name);
    strcpy(rooms_avl[actual_rooms], new_room);
    actual_rooms +=1;
    coap_set_status_code(response, CREATED_2_01);
  }else{
	  coap_set_status_code(response, BAD_REQUEST_4_00);
  }
}

static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	size_t len = 0;
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
	}
}

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
  const char *room = NULL;
  int length;
  int index;
  /* The query string can be retrieved by rest_get_query() or parsed for its key-value pairs. */
  if(coap_get_query_variable(request, "room", &room)) {
    index = atoi(room);
    if(index > 0 && index < actual_rooms+1) {
      	char msg[];
	strcpy(msg,"{\"MoteValue\":{\"MoteName\":");
	strcat(msg,rooms_avl[index-1]);
	strcat(msg,",\"Value\":\"10\"}}");
      	length = sizeof(msg);
      	memcpy(buffer, msg, length-1);
    } else {
    	length = sizeof(rooms_avl);
    	memcpy(buffer, rooms_avl, length);
    }
  }else{
	length = sizeof(rooms_avl);
	memcpy(buffer, rooms_avl, length);
  }
  coap_set_header_content_format(response, TEXT_PLAIN); /* text/plain is the default, hence this option could be omitted. */
  coap_set_header_etag(response, (uint8_t *)&length, 1);
  coap_set_payload(response, buffer, length);
}
