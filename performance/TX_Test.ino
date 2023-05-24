#include <SPI.h>
#include <LoRa.h>


#define LORA_SS 53
#define LORA_RST 9
#define LORA_DIO0 8


void initLoRaMega()
{
  pinMode(LORA_SS, OUTPUT);
  digitalWrite(LORA_SS, HIGH);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);
}



uint8_t counter = 0;

void setup() {
  Serial.begin(9600);
  while (!Serial);
  initLoRaMega();
  Serial.println("LoRa Sender");

  // SET FREQUENCY
  if (!LoRa.begin(433E6)) {
    Serial.println("Starting LoRa failed!");
    while (1);
  }

  // CONFIGURATION
  LoRa.setSpreadingFactor(12);
  LoRa.setSignalBandwidth(125E3);
  LoRa.setCodingRate4(5);
  LoRa.setPreambleLength(8);
  LoRa.setSyncWord(0x34);
  LoRa.enableCrc();
  LoRa.setTxPower(20);
  Serial.println("Setting done!");
}

void loop() {
  if(counter < 51){
    Serial.print("Sending packet: ");
    Serial.println(counter);
    uint8_t buffer_x[10] = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, counter};
    // send packet
    LoRa.beginPacket();
    LoRa.write(buffer_x, 10);
    LoRa.endPacket();
  }
  counter++;  
  delay(10000);
}
