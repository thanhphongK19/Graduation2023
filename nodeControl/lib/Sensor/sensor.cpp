#include "sensor.h"




#if 0 
// void serialEvent()
// {
//   inputString = Serial.readStringUntil(13);           //read the string until we see a <CR>
//   input_string_complete = true;
// }

// void serialEvent3()
// {
//   sensorString = Serial3.readStringUntil(13);
//   sensor_string_complete = true;
// }


// int read_WaterLevel()
// {
//   Serial.println( hc.dist());

//     // unsigned long duration; // biến đo thời gian
//     // int distance;           // biến lưu khoảng cách
    
//     // /* Phát xung từ chân trig */
//     // digitalWrite(trig,0);   // tắt chân trig
//     // delayMicroseconds(2);
//     // digitalWrite(trig,1);   // phát xung từ chân trig
//     // delayMicroseconds(5);   // xung có độ dài 5 microSeconds
//     // digitalWrite(trig,0);   // tắt chân trig
    
//     // /* Tính toán thời gian */
//     // // Đo độ rộng xung HIGH ở chân echo. 
//     // duration = pulseIn(echo,HIGH);  
//     // delay(200);
//     // // Tính khoảng cách đến vật.
//     // distance = int(duration/2/29.412);
//     // Serial.println(distance);
//     // if (distance >= '?')
//     // {
//     //   waterLevel = 0;
//     // }
//     // else if (distance <= '?')
//     // {
//     //   waterLevel = 1;
//     // }
//     // return waterLevel;
// }


// void read_pH(void){
//   open_channel(2);
//   if(input_string_complete == true){
//     Serial3.print(inputString);
//     Serial3.print('\r');
//     inputString = "";
//     input_string_complete = false;
//   }
//   if(sensor_string_complete == true ){
//     Serial.print("pH = ");
//     Serial.println(sensorString);
//   }
//   sensor_string_complete = false;
//   sensorString = "";
// }

// void read_ORP(void)
// {
//   open_channel(1);
//   if(input_string_complete == true){
//     Serial3.print(inputString);
//     Serial3.print('\r');
//     inputString = "";
//     input_string_complete = false;
//   }
//   if(sensor_string_complete == true ){
//     Serial.print("ORP = ");
//     Serial.println(sensorString);
//   }
//   sensor_string_complete = false;
//   sensorString = "";
// }


// void open_channel(int channel){
//   switch (channel) {
//     case 1:
//       Serial.println("open Channel 1");
//       digitalWrite(X, LOW);
//       digitalWrite(Y, LOW); 
//       break;
//     case 2:
//       Serial.println("open Channel 2");
//       digitalWrite(X, LOW);
//       digitalWrite(Y, HIGH);
//       break;
//     case 3:
//       digitalWrite(X, HIGH);
//       digitalWrite(Y, LOW);
//       break;
//     case 4:
//       digitalWrite(X, HIGH);
//       digitalWrite(Y, HIGH);
//       break;
//     default:
//       break;
//   }
// }
#endif