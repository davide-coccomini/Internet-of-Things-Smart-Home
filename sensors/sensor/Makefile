CONTIKI_PROJECT = client
all: $(CONTIKI_PROJECT)
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"
CONTIKI = ../../..

# Include optional target-specific module
include $(CONTIKI)/Makefile.identify-target
MODULES_REL += $(TARGET)

include $(CONTIKI)/Makefile.dir-variables
MODULES += $(CONTIKI_NG_APP_LAYER_DIR)/coap

include $(CONTIKI)/Makefile.include
