#include "rtc.h"

#define DS3231_ADDRESS 0x68
///////////////////////////////////////////////////////////////////////////////////////


struct timeRTC{
  unsigned char date;
  unsigned char day;
  unsigned char month;
  unsigned char year;
  unsigned char hour;
  unsigned char minute;
  unsigned char second;
};
struct timeRTC timeNow;


int BCDtoDec(byte num)
{
  return ((num/16)*10 + (num%16));
 
}
int DectoBCD (byte num)
{
  return ((num/10)*16 + (num%10));
}

///////////////////////////////////////////////////////////////////////////////////////
void setTime(unsigned char hour, unsigned char minute, unsigned char second)
{
  Wire.beginTransmission(DS3231_ADDRESS);
  Wire.write(0);
  Wire.write(DectoBCD(second));
  Wire.write(DectoBCD(minute));
  Wire.write(DectoBCD(hour));
  Wire.write(DectoBCD(2));
  Wire.write(DectoBCD(24));
  Wire.write(DectoBCD(4));
  Wire.write(DectoBCD(23));
  Wire.endTransmission();
}

////////////////////////////////////////////////////////////////////////////////////////
void getTime()
{

  Wire.beginTransmission(DS3231_ADDRESS);

  Wire.write(0);

  Wire.endTransmission();


  Wire.requestFrom(DS3231_ADDRESS, 3);

  timeNow.second = BCDtoDec(Wire.read());
  timeNow.minute = BCDtoDec(Wire.read());
  timeNow.hour = BCDtoDec(Wire.read()&0x3f);

}

///////////////////////////////////////////////////////////////////////////////////////
unsigned char alarm(unsigned char endhour, unsigned char endminute, unsigned char endSecond)
{
  unsigned char flag;

	//Read Time from RTC
  getTime(); 
  Serial.print(timeNow.hour);
  Serial.print(timeNow.minute);
  Serial.println(timeNow.second);


	if(timeNow.hour == endhour){
		if(timeNow.minute == endminute){
      if(timeNow.second < endSecond){
        flag = 1;     // continnue alarm
      }
			else{
        flag = 0;
      }
		}
		else{
			flag = 1;     // continue alarm
		}
	}
	else{
		flag = 1;         // continnue alarm
	}
	return flag;
}
///////////////////////////////////////////////////////////////////////////////////////