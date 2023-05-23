#include <Arduino.h>
#include <SPI.h>              
#include <LoRa.h>
#include <DHT.h>
#include <DS18B20.h>

#define FREQUENCY   433E6
#define SF          7
#define BW          125E3
#define CR          5
#define PL          8   // Preample Length
#define SYNCWORD    0x34

#define SOILMOISTURE      A0
#define DS18B20PIN        3

#define DHTPIN            4      
#define DHTTYPE           DHT11

DHT dht(DHTPIN, DHTTYPE);
DS18B20 ds(DS18B20PIN);

uint8_t ControlNode = 100;
uint8_t SensorNode1 = 101;
uint8_t SensorNode2 = 102;
uint8_t Gateway = 200;
String outgoing;              // outgoing message
uint8_t msgCount = 0;            // count of outgoing messages
String incoming = "";

unsigned long previousMillis = 0;
unsigned long int previoussecs = 0;
unsigned long int currentsecs = 0;
unsigned long currentMillis = 0;

int soilMoistureValue = 0;
int soilMoisture = 0;
int soilTemp;
int airTemp;
int airHumid;
int status;
String Mymessage = "";
void sendMessage(String outgoing);

void setup() {
  Serial.begin(9600);                   // initialize serial
  dht.begin();

  SPILoRaConfig();
}

void loop() {
           
  // parse for a packet, and call onReceive with the result:
  onReceive(LoRa.parsePacket());
  //sendMessage("hello");
  //delay(5000);
}

void SPILoRaConfig() {
    if (!LoRa.begin(FREQUENCY)) {   // failed to start LoRa
        Serial.println("Starting LoRa failed!");
        while(1);
    }

    LoRa.setSpreadingFactor(SF);
    LoRa.setSignalBandwidth(BW);
    LoRa.setCodingRate4(CR);
    LoRa.setPreambleLength(PL);
    LoRa.setSyncWord(SYNCWORD);
    LoRa.enableCrc();

 
    Serial.println("Setting done!!");
    
}

void readSensorValue(){
  soilMoistureValue = analogRead(SOILMOISTURE);  //put Sensor insert into soil
  soilMoisture = map(soilMoistureValue, 0, 1023, 0, 100);
  soilTemp = ds.getTempC();
  airHumid = dht.readHumidity();
  airTemp = dht.readTemperature();
  status = 1;
}

void onReceive(int packetSize) {
  if (packetSize == 0) return;          // if there's no packet, return
  
  String incoming = "";

  while (LoRa.available()) {
    incoming += (char)LoRa.read();
  }
  Serial.println(incoming);
  //String v = getValue(incoming, ' ', 0);
  int Val = incoming.toInt();
  if (Val == 101)
  {
    Serial.print("Incoming:");
    Serial.println(incoming);
    readSensorValue();
    Mymessage = Mymessage+ "101 " + soilMoisture + " " + soilTemp + " " + airHumid + " " + airTemp + " " + status;
    sendMessage(Mymessage);
    Serial.print("Sent!: ");
    Serial.println(Mymessage);
    delay(100);
    Mymessage = "";
  }
}

void sendMessage(String outgoing) {
  LoRa.beginPacket();                   // start packet
  LoRa.print(outgoing);                 // add payload
  LoRa.endPacket();                     // finish packet and send it
  msgCount++;                           // increment message ID
}

String getValue(String data, char separator, int index)
{
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