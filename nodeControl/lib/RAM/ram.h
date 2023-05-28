#include <Arduino.h>

/* define valve output */
#define valveWater 10
#define valveNutrition 11
#define valveTank 12

#define on HIGH
#define off LOW

/* for pump nutrition*/
#define eHourWater        7
#define eMinuteWater      0
#define eSecondWater      10
#define eHourNutrition    7
#define eMinuteNutrition  0
#define eSecondNutrition  5



/*define for pinout mega2560 and lora Ra02*/
#define LORA_SS 53
#define LORA_RST 9
#define LORA_DIO0 8


////////////////////////////////////////////////////////////////////////
/* define for lora */
#define ADDRESS     "100"
#define FREQUENCY   433E6
#define SF          7
#define BW          125E3
#define CR          5
#define PL          8   // Preample Length
#define SYNCWORD    0x12
////////////////////////////////////////////////////////////////////////


//#define RX              0
//#define TX              1

// #define trigPin         4
// #define echoPin         5


#define RELAY1          5
#define RELAY2          6
#define RELAY3          7



#define rXSensor        19
#define tXSensor        18

#define X               2
#define Y               3

