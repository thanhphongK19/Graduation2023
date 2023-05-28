#include <Arduino.h>
#include <SPI.h>
#include <LoRa.h>
#include "ram.h"

extern void initLora();
extern void SPILoRaConfig();

extern String onReceive(int packetSize);
extern void sendMessage(String outgoing);

extern String getValue(String data, char separator, int index);


