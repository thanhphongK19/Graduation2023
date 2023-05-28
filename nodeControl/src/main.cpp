#include <Arduino.h>
#include <Wire.h>
#include <HCSR04.h>


#include "rtc.h"
#include "ram.h"
#include "sensor.h"
#include "loraprotocol.h"
#include "sensor.h"
#include <LoRa.h>


///////////////////////////////////////////////////////////////////////////////////////

unsigned long previous;
unsigned long current;
unsigned long interval = 1000;


unsigned char statusNode = 1;
unsigned char onlyOne = 0;
String dataIncoming = "";

const int waterFull = 1;
const int waterEmpty  = 6;

struct valve{
  unsigned char status;
  unsigned char alarm;
  unsigned char startHour;
  unsigned char startMinute;
  unsigned char startSecond;
  unsigned char endHour;
  unsigned char endMinute;
  unsigned char endSecond;
};
valve valueValve_Water;
valve valueValve_Nutrition;
valve valueValve_Maintank;

unsigned char waterLevelMainTank;
/*
  waterLevelMainTank = 1 => Main tank full water
  waterLevelMainTank = 0 => Main tank empty water
*/

struct datafromGateway{
  unsigned char pump;
  unsigned char nutrition;
  unsigned char alarmPump;
  unsigned char startHour;
  unsigned char startMinute;
  unsigned char endHour;
  unsigned char endMinute;
};
struct datafromGateway dataControl;

struct flag{
  unsigned char receivePacketBroadCast;
  unsigned char receivePacketControl;
  unsigned char pumpNutritions;
  unsigned char waterLevel;
  unsigned char mainTank;
};
struct flag glFlag;
///////////////////////////////////////////////////////////////////////////////////////



///////////////////////////////////////////////////////////////////////////////////////
void setup();
void loop();
void operate();

void onReceiveCallBack(int packetSize);


int queryDataLoRa(String data);
void separateString(String stringData);
void queryDataControl();

void upLoadStateData();

unsigned char pumpNutrition();
unsigned char readWaterLevel();

/*set value for valve */
void setValueVavle(unsigned char vavle, unsigned char mode);
void alarming(unsigned char valve, unsigned char *statusAlarm, unsigned char hour, unsigned char minute, unsigned char second);


void clearStructData();
///////////////////////////////////////////////////////////////////////////////////////
  
  
HCSR04 hc(4,5);


int counter = 0;
//String incoming1 = "0 0 1 7 0 7 30";
void setup()
{ 
  Serial.begin(9600);
  Wire.begin();

  initLora();
  // LoRa.onReceive(onReceiveCallBack);
  // LoRa.receive();

  pinMode(valveWater,OUTPUT);
  pinMode(valveNutrition,OUTPUT);
  pinMode(valveTank,OUTPUT);

  pinMode(13,OUTPUT);


}
int oie = 0;
void loop()
{    
  operate();

  /* blink led GPIO 13 every 1s */
  current = millis();
  if(current - previous >= 1000){
    //upLoadStateData();
    if(oie == 1){
      digitalWrite(13,HIGH);
      // setValueVavle(valveNutrition,on);
      // setValueVavle(valveWater,on);
      // setValueVavle(valveTank,on);
      oie = 0;
    }
    else{
      digitalWrite(13,LOW);
      // setValueVavle(valveNutrition,off);
      // setValueVavle(valveWater,off);
      // setValueVavle(valveTank,off);
      oie = 1;
    }
    previous = current;
  }
  
  /* receive data from gateway ?*/
  dataIncoming = onReceive(LoRa.parsePacket());
  if(dataIncoming != "0"){
    //Serial.println("dataincoming LoRa");
    queryDataLoRa(dataIncoming);
    if(glFlag.receivePacketBroadCast == 1){
      Serial.println("receive Broadcast");
          
      upLoadStateData();
      //LoRa.receive();
      glFlag.receivePacketBroadCast = 0;
    }
    else if(glFlag.receivePacketControl == 1){
      Serial.println("receive dataControl");

      separateString(dataIncoming);
      queryDataControl();

      upLoadStateData();
      //LoRa.receive();
      glFlag.receivePacketControl = 0;
    }
  }

  //Serial.println(hc.dist());
  //queryDataLoRa("101 1 0 1 0");

  //delay(1000);
}

void onReceiveCallBack(int packetSize)
{
  Serial.println("receive");
  if(packetSize == 0) return;
  

  //dataIncoming = onReceive(LoRa.parsePacket());

  while (LoRa.available()) {
    dataIncoming += (char)LoRa.read();
  }

   if(dataIncoming != "0"){
      //Serial.println("dataincoming LoRa");
      queryDataLoRa(dataIncoming);
      if(glFlag.receivePacketBroadCast == 1){
        Serial.println("receive Broadcast");
        
        upLoadStateData();
        glFlag.receivePacketBroadCast = 0;
      }
      else if(glFlag.receivePacketControl == 1){
        Serial.println("receive dataControl");

        separateString(dataIncoming);
        queryDataControl();

        upLoadStateData();
        glFlag.receivePacketControl = 0;
      }
  }
}

void operate()
{ 
  
  alarming(valveWater,&valueValve_Water.alarm,valueValve_Water.endHour,valueValve_Water.endMinute,valueValve_Water.endSecond);
  alarming(valveNutrition,&valueValve_Nutrition.alarm,valueValve_Nutrition.endHour,valueValve_Nutrition.endMinute,valueValve_Nutrition.endSecond);
  alarming(valveTank,&valueValve_Maintank.alarm,valueValve_Maintank.endHour,valueValve_Maintank.endMinute,valueValve_Maintank.endSecond);

  if(glFlag.pumpNutritions == 1){
    glFlag.pumpNutritions = pumpNutrition();
  }

}

unsigned char pumpNutrition()
{
  unsigned char flag;
  flag = 1;
  if(valueValve_Water.alarm == 0 && valueValve_Nutrition.alarm == 0 && onlyOne == 0){
      Serial.println("stop mix nutrition and start pump");

      setValueVavle(valveTank,on);
      /*set status valve*/
      valueValve_Maintank.status = 1;

      waterLevelMainTank = readWaterLevel();

      /* Second return data for user */
      upLoadStateData();
      //LoRa.receive();
      onlyOne = 1;
      return 1;
  }


  if(valueValve_Maintank.status == 1){
     glFlag.waterLevel = readWaterLevel();
     if(glFlag.waterLevel == 0){

      /* close valve main tank end stop pump*/
      setValueVavle(valveTank,off);

      valueValve_Maintank.status = 0;

      /* Third return data for user */
      upLoadStateData();
      onlyOne = 0;
      flag = 0;
    }
  }

  return flag;
}

/* Read level water of main tank */
unsigned char readWaterLevel()
{
  unsigned char flag;

  /* read utral sonic sensor */
  float a = hc.dist();

  /* convert float to int */
  int waterLevel = (int)a;
  Serial.println(waterLevel);
  flag = 1;

  if(waterLevel < 20){
    /* main tank full water */
    Serial.println("Main tank have the water");
    flag = 1;
  }
  else if(waterLevel > 21){
    /* main tank empty water */
    Serial.println("empty water and stop pump");
    flag = 0;
  }
  return flag;
}


void upLoadStateData()
{
  statusNode = 1;
  String message = "";
  message = message + ADDRESS + ' ' + valueValve_Water.status  + ' ' + valueValve_Nutrition.status   + ' ' + valueValve_Maintank.status  + ' ' + waterLevelMainTank  + ' ' + statusNode;
  
  Serial.println(message);

  /* send message by lora */
  sendMessage(message);

  LoRa.receive();
}


int queryDataLoRa(String data)
{

  if((getValue(data,' ',0)).toInt() == 101 || (getValue(data, ' ',0)).toInt() == 102){
    //Serial.println("Receive data from Sensor");
    return 0;     
  }
  else if(data.toInt() == 100){
    glFlag.receivePacketBroadCast = 1;
    //Serial.println("broadcast");
  }
  else{
    glFlag.receivePacketControl = 1;
    //Serial.println("Data control");
  }
}

void separateString(String stringData)
{
  String pump = getValue(stringData, ' ', 0);
  dataControl.pump = pump.toInt();

  String nutrition = getValue(stringData, ' ', 1);
  dataControl.nutrition = nutrition.toInt();

  String alarmPump = getValue(stringData, ' ', 2);
  dataControl.alarmPump = alarmPump.toInt();

  String startHour = getValue(stringData, ' ', 3);
  dataControl.startHour = startHour.toInt();

  String startMinute = getValue(stringData, ' ', 4);
  dataControl.startMinute = startMinute.toInt();

  String endHour = getValue(stringData, ' ', 5);
  dataControl.endHour = endHour.toInt();

  String endMinute = getValue(stringData, ' ', 6);
  dataControl.endMinute = endMinute.toInt();
}

void queryDataControl()
{
  /* off Pump */
	if(dataControl.pump  == 0 && dataControl.alarmPump == 0 && dataControl.nutrition == 0){
		setValueVavle(valveNutrition,off);
    setValueVavle(valveWater,off);
    setValueVavle(valveTank,off);

    clearStructData();
  }
	else if(dataControl.pump == 0 && dataControl.alarmPump == 1 && dataControl.nutrition == 0){
    /* alarm pump water */
    /* open valve water and valve main tank */
    setValueVavle(valveWater,on);
    //setValueVavle(valveTank,on);

    setTime(dataControl.startHour,dataControl.startMinute,0);
    Serial.println("set RTC and start Alarm \r\n");

    /* set value valve water */
    valueValve_Water.status = 1;
    valueValve_Water.alarm = 1;
    valueValve_Water.startHour = dataControl.startHour;
    valueValve_Water.startMinute = dataControl.startMinute;
    valueValve_Water.endHour = dataControl.endHour;
    valueValve_Water.endMinute = dataControl.endMinute;

     /* set value valve maintank */
    valueValve_Maintank.status = 1;
    valueValve_Maintank.alarm = 1;
    valueValve_Maintank.startHour = dataControl.startHour;
    valueValve_Maintank.startMinute = dataControl.startMinute;
    valueValve_Maintank.endHour = dataControl.endHour;
    valueValve_Maintank.endMinute = dataControl.endMinute;

	}
	else if(dataControl.pump == 1 && dataControl.alarmPump == 0 && dataControl.nutrition == 0){
    /* free pump water */
    Serial.println("start free pump water \r\n");

    setValueVavle(valveWater,on);
    //setValueVavle(valveNutrition,on);
    setValueVavle(valveTank,on);
    
    /* set value valve water */
    valueValve_Water.status = 1;
    valueValve_Water.alarm = 0;
    valueValve_Water.startHour = 0;
    valueValve_Water.startMinute = 0;
    valueValve_Water.endHour = 0;
    valueValve_Water.endMinute = 0;

    /* set value valve maintank */
    valueValve_Maintank.status = 1;
    valueValve_Maintank.alarm = 0;
    valueValve_Maintank.startHour = 0;
    valueValve_Maintank.startMinute = 0;
    valueValve_Maintank.endHour = 0;
    valueValve_Maintank.endMinute = 0;
	}
  else if(dataControl.pump == 0 && dataControl.alarmPump == 0 && dataControl.nutrition == 1){
    /* pump pH*/
    Serial.println("start pump nutrition \r\n");
    glFlag.pumpNutritions = 1;

    setValueVavle(valveWater,on);
    setValueVavle(valveNutrition,on);

    setTime(7,0,0);

    /* set value valve water */
    valueValve_Water.status = 1;
    valueValve_Water.alarm = 1;
    valueValve_Water.startHour = 7;
    valueValve_Water.startMinute = 0;
    valueValve_Water.startSecond = 0;
    valueValve_Water.endHour = eHourWater;
    valueValve_Water.endMinute = eMinuteWater;
    valueValve_Water.endSecond = eSecondWater;

    /* set value valve Nutrition */
    valueValve_Nutrition.status = 1;
    valueValve_Nutrition.alarm = 1;
    valueValve_Nutrition.startHour = 7;
    valueValve_Nutrition.startMinute = 0;
    valueValve_Nutrition.startSecond = 0;
    valueValve_Nutrition.endHour = eHourNutrition;
    valueValve_Nutrition.endMinute = eMinuteNutrition;
    valueValve_Nutrition.endSecond = eSecondNutrition;
	}
}

void setValueVavle(unsigned char vavle, unsigned char mode)
{
  digitalWrite(vavle,mode);
}

void alarming(unsigned char valve, unsigned char *statusAlarm, unsigned char hour, unsigned char minute, unsigned char second)
{
  if(*statusAlarm == 1){
    *statusAlarm = alarm(hour,minute,second);

    if(*statusAlarm == 0){
      setValueVavle(valve,off);

      if(valve == valveWater){
        valueValve_Water.status = 0;
      }
      else if(valve == valveNutrition){
        valueValve_Nutrition.status = 0;
      }
      else if(valve == valveTank){
        valueValve_Maintank.status = 0;
      }
      //printf("finish alarm \r\n");
    }
  }
}

void clearStructData()
{
  valueValve_Water.status = 0;
  valueValve_Water.alarm = 0;
  valueValve_Water.startHour = 0;
  valueValve_Water.startMinute = 0;
  valueValve_Water.startSecond = 0;
  valueValve_Water.endHour = 0;
  valueValve_Water.endMinute = 0;
  valueValve_Water.endSecond = 0;

  valueValve_Maintank.status = 0;
  valueValve_Maintank.alarm = 0;
  valueValve_Maintank.startHour = 0;
  valueValve_Maintank.startMinute = 0;
  valueValve_Maintank.startSecond = 0;
  valueValve_Maintank.endHour = 0;
  valueValve_Maintank.endMinute = 0;

  valueValve_Nutrition.status = 0;
  valueValve_Nutrition.alarm = 0;
  valueValve_Nutrition.startHour = 0;
  valueValve_Nutrition.startMinute = 0;
  valueValve_Nutrition.startSecond = 0;
  valueValve_Nutrition.endHour = 0;
  valueValve_Nutrition.endMinute = 0;
  valueValve_Nutrition.endSecond = 0;
}