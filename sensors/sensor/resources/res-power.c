#include "contiki.h"
#include "coap-engine.h"
#include <string.h>

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP
static char outlets_avl[6][15] = {
        "fridge, ",
        "oven, "
    };
static int actual_outlets = 2;
static int max_outlets = 7;
static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

/* A simple actuator example, depending on the color query parameter and post variable mode, corresponding led is activated or deactivated */
RESOURCE(res_power,
         "title=\"Power: ?outlet=0..\" POST/PUT name=<name>&value=<value>\";rt=\"Control\"",
	 res_get_handler,
         res_post_handler,
         res_put_handler,
         NULL);

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
  const char *name = NULL;
  if(coap_get_post_variable(request, "name", &name)&& actual_outlets <= max_outlets) {
    char new_outlet[15];
    sprintf(new_outlet, "%s, ", name);
    strcpy(outlets_avl[actual_outlets], new_outlet);
    actual_outlets += 1;
    coap_set_status_code(response, CREATED_2_01);
  }else{
    coap_set_status_code(response, BAD_REQUEST_4_00);
  }
}
static void res_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset){
	size_t len = 0;
	const char *text = NULL;
	char outlet[15];
    	memset(outlet, 0, 15);
	char power[32];
    	memset(power, 0, 32);
	int success_1 = 0;
	int success_2 = 0;

	len = coap_get_post_variable(request, "name", &text);
	if(len > 0 && len < 15) {
	    memcpy(outlet, text, len);
	    success_1 = 1;
	}

	len = coap_get_post_variable(request, "value", &text);
	if(len > 0 && len < 32 && success_1 == 1) {
		memcpy(power, text, len);
		char msg[50];
	    memset(msg, 0, 50);
		sprintf(msg, "Power in %s set to %s", outlet, power);
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
  const char *outlet = NULL;
  int length;
  int index;
  /* The query string can be retrieved by rest_get_query() or parsed for its key-value pairs. */
  if(coap_get_query_variable(request, "outlet", &outlet)) {
    index = atoi(outlet);
    if(index > 0 && index < actual_outlets+1) {
      	length = sizeof(outlets_avl[index-1]);
      	memcpy(buffer, outlets_avl[index-1], length);
    } else {
	length = sizeof(outlets_avl);
	memcpy(buffer, outlets_avl, length);
    }
  } 
  else{
	length = sizeof(outlets_avl);
	memcpy(buffer, outlets_avl, length);
  }
  coap_set_header_content_format(response, TEXT_PLAIN);
  coap_set_header_etag(response, (uint8_t *)&length, 1);
  coap_set_payload(response, buffer, length);
}
