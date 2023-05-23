
#include "LoRa.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "socketClient.h"

#define ss   10             //GPIO 10
#define rst  6              //GPIO  6
#define dio0 24             //GPIO  4

#define FREQUENCY   433E6
#define SF          7
#define BW          125E3
#define CR          5
#define PL          8        // Preample Length
#define SYNCWORD    0x34

String controlNodeAddress = "100";
String sensorNode1Address = "101";
String sensorNode2Address = "102";
String Gateway = "200";
String outgoing;              // outgoing message
int msgCount = 0;            // count of outgoing messages



char statusControlNode[20] = "100 0 0 0 0 0";
char statusSenSorNode1[20] = "101 0 0 0 0 0";
char statusSenSorNode2[20] = "102 0 0 0 0 0";


char incoming[20] = "";
char previousIncoming[20] = "";
/*
 * incoming[0] -> incoming[2] = address node
 * incoming[3]                = " "
 * incoming[4]                = status valve Water
 * incoming[5]                = " "
 * incoming[6]                = status valve Nutrition
 * incoming[7]                = " "
 * incoming[8]                = status valve Maintank
 * incoming[9]                = " "
 * incoming[10]               = Water level of Main tank
 * incoming[11]                = " "
 * incoming[12]               = status node
 * */
char *dataSocket  ;

String SenderNode = "";

// Tracks the time since last event fired
// unsigned long previousMillis = 0;
// unsigned long previoussecs = 0;
// unsigned long currentsecs = 0;
// unsigned long currentMillis = 0;
// unsigned long interval = 1; 
// int Secs = 0;

int previousPacket = 0;

/* Variable for millis */
unsigned long previousMillis = 0;
unsigned long currentMillis  = 0;


String Mymessage = "";

bool is_packets_from_server = false;

unsigned char key3 = 0;
unsigned char key = 0;
unsigned char key1 = 0;
unsigned char key4 = 0;

const unsigned long loop_BroadCast = 10000;
const unsigned char timeOutBroadCast = 5; 

struct count{
    unsigned long previous ;
    unsigned long current ;
};
struct count timerControlNode;
struct count timerSenSor1Node;
struct count timerSenSor2Node;
struct count timerBroadCast;



struct flag{
    unsigned char BroadCast = 1;
    unsigned char startControlNode = 0;
    
    unsigned char controlNode = 1;
    unsigned char sensorNode1 = 0;
    unsigned char sensorNode2 = 0;
    unsigned char expiredTimeOut = 0;

    unsigned char receiveControlNode = 0;
    unsigned char receiveSensorNode1 = 0;
    unsigned char receiveSensorNode2 = 0;
    
    unsigned char endLoopBroadCast = 0;
};
struct flag glFlag;
///////////////////////////////////////////////////////////////////////////////////////

void loop();
void operate();
void SPILoRaConfig();
void sendMessage(String outgoing); 
void onReceive(int packetSize);

void queryDataLoRa();
void sendBroadcast(String address, unsigned char minuteTimeOut);
unsigned char counterTimer(unsigned char second);
String getValue(String data, char separator, int index);
void clearData();
///////////////////////////////////////////////////////////////////////////////////////
void init()
{
  timerControlNode.current = 0;
  timerControlNode.previous = 0;

  timerSenSor1Node.current = 0;
  timerSenSor1Node.previous = 0;

  timerSenSor2Node.current = 0;
  timerSenSor2Node.previous = 0;

  timerBroadCast.current = 0;
  timerBroadCast.previous = 0;
}
void setup() {

    //printf("LoRa Gatway!\n");
    
    setupSocket();
    createThread();
    LoRa.setPins(10, 6, 24);
    SPILoRaConfig();
    init();
}

int main(void)
{
    setup();
    //setTimeout();
    while(1){
        loop();
    }
}

void loop() {

    /* receive packet from Node ?*/
    onReceive(LoRa.parsePacket());
    
    #if 1
    /* receive packet from thread python */
    is_packets_from_server = receivePacketFromServer();
    if(is_packets_from_server == true){
      is_packets_from_server = false;
      
      dataSocket = receiveSocket();
      printf("%s \r\n",dataSocket);
      
      /* stop broadcast */
      glFlag.BroadCast = 0;
        
      /* start execute node Control */
      glFlag.startControlNode = 1;
      //printf("start control \r\n");
   
      
      /* send data control to node control */
      sendMessage(dataSocket);
      
      
      /* clear buffer socket and flag */
      clearBuffer();
    }
    #endif
    

    operate();
    
}

void operate()
{
    /*timerBroadCast.current = millis();
    if(timerBroadCast.current - timerBroadCast.previous >= loop_BroadCast){
        timerBroadCast.previous = timerBroadCast.current;
        glFlag.BroadCast = 1;
    }*/
    
    if(glFlag.BroadCast == 1){
        if(glFlag.controlNode == 1){
            sendBroadcast(controlNodeAddress,timeOutBroadCast);
        }
        else if(glFlag.sensorNode1 == 1){
            sendBroadcast(sensorNode1Address,timeOutBroadCast);
        }
        else if(glFlag.sensorNode2 == 1){
            sendBroadcast(sensorNode2Address,timeOutBroadCast);
        }

    }

}


/* config module lora RA02*/
void SPILoRaConfig() {
    if (!LoRa.begin(FREQUENCY)) {   // failed to start LoRa
     
      printf("Starting LoRa failed!");
        
      while(1);
    }

    LoRa.setSpreadingFactor(7);
    LoRa.setSignalBandwidth(125E3);
    LoRa.setCodingRate4(5);
    LoRa.setPreambleLength(8);
    LoRa.setSyncWord(0x34);
    LoRa.enableCrc();
    printf("Init LoRa Gateway Done !\n");

}

void sendMessage(String outgoing) {
    LoRa.beginPacket();                   // start packet
    LoRa.print(outgoing);                 // add payload
    LoRa.endPacket();                    // finish packet and send it
}

void sendBroadcast(String address, unsigned char TimeOut){
    static unsigned char onlyOne = 0;

    if(onlyOne == 0){
        sendMessage(address);
        //printf("send \r\n");
        onlyOne = 1;
    }

    if(address == "100"){
        if(counterTimer(TimeOut) == 1){
            glFlag.controlNode = 0;
            glFlag.sensorNode1 = 1;
            glFlag.sensorNode2 = 0;
            
            /*if no respond from node,it will send packet disconnect to server*/
            if(glFlag.receiveControlNode == 0){
              
              sendSocket(statusControlNode);
            }
            else{
              glFlag.receiveControlNode = 0;
            }
          
            onlyOne = 0;
        } 
    }
    else if(address == "101"){   
        if(counterTimer(TimeOut) == 1){
            glFlag.sensorNode1 = 0;
            glFlag.sensorNode2 = 1;
            glFlag.controlNode = 0;
            
            /*if no respond from node,it will send packet disconnect to server*/
            if(glFlag.receiveSensorNode1 == 0){
              sendSocket(statusSenSorNode1);
            }
            else{
              glFlag.receiveSensorNode1 = 0;
            }
            

            onlyOne = 0;
        } 
    }
    else if(address == "102"){   
        if(counterTimer(TimeOut) == 1){
            glFlag.sensorNode2 = 0;
            glFlag.controlNode = 1;
            glFlag.sensorNode1 = 0;
            
            /*if no respond from node,it will send packet disconnect to server*/
            if(glFlag.receiveSensorNode2 == 0){
              sendSocket(statusSenSorNode2);
              glFlag.endLoopBroadCast = 1;
            }
            else{
              glFlag.receiveSensorNode2 = 0;
            }
   
            onlyOne = 0;
            
        } 
    }
        
}


void onReceive(int packetSize) {
  // received a packet-D_GNU_SOURCE
 
  if(packetSize){
  
      printf("Received packet: ");

      int i = 0;
      while (LoRa.available()) {
        incoming[i] = (char)LoRa.read();
        //printf("%c", (char)LoRa.read());
        i++;
      }
      
      // print RSSI of packet
      //printf("' with RSSI ");
      //printf("%d\n",LoRa.packetRssi());
         
      if(strcmp(incoming, previousIncoming) != 0 || glFlag.endLoopBroadCast == 1)
      {
        sendSocket(incoming);
        strcpy(previousIncoming,incoming);
        printf("%s \r\n", previousIncoming);
        
        if(glFlag.endLoopBroadCast == 1){
          glFlag.endLoopBroadCast = 0;
        }
      }

      queryDataLoRa();
      clearData();
  }

  
}
//////////////////////////////////////////////////////////////////////////////////////

void queryDataLoRa()
{
  if(incoming[2] == '0'){

    glFlag.receiveControlNode = 1;
    if(glFlag.startControlNode == 1){
      if(incoming[4] == '0' && incoming[6] == '0' && incoming[8] == '0' && incoming[10] == '0'){
        /* stop excute node control */
        glFlag.startControlNode = 0;
        
        /* restart broadcast */
        glFlag.BroadCast = 1;
        //printf(" restart BroadCast \r\n");

      }
    }
    
  }
  else if(incoming[2] == '1'){
    glFlag.receiveSensorNode1 = 1;
  }
  else if(incoming[2]  == '2'){
    glFlag.receiveSensorNode2 = 1;
  }
}

/* set time out for send broadcast */
unsigned char counterTimer(unsigned char second){
    unsigned long interval;
    unsigned char flag;

    interval = second*1000;

    flag = 0;
    currentMillis = millis();
    if(currentMillis - previousMillis >= interval){
        previousMillis = currentMillis;

        flag = 1;
    }
    
    return flag;
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

void clearData()
{
  for(int i = 0;i< 20;i++){
    incoming[i] = 0;
  }
}


