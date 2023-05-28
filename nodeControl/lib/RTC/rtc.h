#include <Arduino.h>
#include <Wire.h>
#include "ram.h"


extern int BCDtoDec(byte num);
extern int DectoBCD (byte num);
extern void setTime(unsigned char hour, unsigned char minute, unsigned char second);
extern void getTime();
extern unsigned char alarm(unsigned char endhour, unsigned char endminute, unsigned char endSecond);
