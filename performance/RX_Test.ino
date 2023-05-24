#include <SPI.h>
#include <LoRa.h>

int data = 0;
void setup() {
  Serial.begin(9600);
  while (!Serial);

  Serial.println("LoRa Receiver");

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
  Serial.println("Setting done!");
}

void loop() {
  // try to parse packet
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    // received a packet
    Serial.print("Received packet '");

    // read packet
    while (LoRa.available()) {
      Serial.print((uint8_t)LoRa.read());
    }

    // print RSSI of packet
    Serial.print(" with RSSI ");
    Serial.print(LoRa.packetRssi());

    Serial.print("  and SNR");
    Serial.println(LoRa.packetSnr());
  }

}
