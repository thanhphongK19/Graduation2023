#include "loraprotocol.h"

byte ControlNode = 0xFF;
byte SensorNode1 = 0xBB;
byte SensorNode2 = 0xCC;
byte Gateway = 0xAA;
String SenderNode = "";


String outgoing;              // outgoing message
byte msgCount = 0;            // count of outgoing messages
String incoming = "";

/* Data control from gateway */
// struct data{
//   unsigned char pump;
//   unsigned char pH;
//   unsigned char alarmPump;
//   unsigned char startHour;
//   unsigned char startMinute;
//   unsigned char endHour;
//   unsigned char endMinute;
// };
// struct data dataFromGateWay;


void initLora(){

  pinMode(LORA_SS, OUTPUT);
  digitalWrite(LORA_SS, HIGH);
  LoRa.setPins(LORA_SS, LORA_RST, LORA_DIO0);

  SPILoRaConfig();
}

void SPILoRaConfig()
{
    if (!LoRa.begin(FREQUENCY)) {   // failed to start LoRa
        Serial.println("Starting LoRa failed!");
        while(1);
    }

    LoRa.setSpreadingFactor(7);
    LoRa.setSignalBandwidth(125E3);
    LoRa.setCodingRate4(5);
    LoRa.setPreambleLength(8);
    LoRa.setSyncWord(0x34);
    LoRa.enableCrc();

    Serial.println("Setting done!!");
}


void sendMessage(String outgoing) {
  LoRa.beginPacket();             // start packet
  LoRa.print(outgoing);           // add payload
  LoRa.endPacket();               // finish packet and send it
  msgCount++;                     // increment message ID
}

String onReceive(int packetSize) {
  if (packetSize == 0) 
    return "0";  // if there's no packet, return
  
  incoming = "";
  while (LoRa.available()) {
    incoming += (char)LoRa.read();
  }
  //incoming = "0 1 0 0 0 0 0";
  Serial.println(incoming);
  //incoming = "";
  return incoming;
}

String getValue(String data, char separator, int index) {
  int found = 0;
  int strIndex[] = { 0, -1 };
  int maxIndex = data.length() - 1;
  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }
  
  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}