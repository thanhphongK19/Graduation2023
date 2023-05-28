package com.example.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver implements MQTTprotocol {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hi", "I am here");

        int endHour = 0;
        int endMinute = 0;
        int alarmPump = 1;

        int mode = intent.getIntExtra("mode",0);
        int startHour = intent.getIntExtra("startHour", 0);
        int startMinute = intent.getIntExtra("startMinute", 0);

        /* alarm Pump Water*/
        if(mode == 0){
            /* Receive data */
            int time = intent.getIntExtra("time", 0);
            int timePlay = Integer.parseInt(String.valueOf(time));

            if ((startMinute + timePlay) < 60) {
                endMinute = startMinute + timePlay;
                endHour = startHour;
            }
            else {
                endMinute = (startMinute + timePlay) - 60;
                endHour = startHour + 1;
            }
            System.out.println(startHour);
            System.out.println(startMinute);
            System.out.println(endHour);
            System.out.println(endMinute);

            String dataControl;
            dataControl = "0 0 1"
                          + " " + String.valueOf(startHour)
                          + " " + String.valueOf(startMinute)
                          + " " + String.valueOf(endHour)
                          + " " + String.valueOf(endMinute);
            MQTTprotocol.publishData("zoneControl/dataControl",dataControl);

//            MQTTprotocol.publishData("zoneControl/startHour",String.valueOf(startHour));
//            MQTTprotocol.publishData("zoneControl/startMinute",String.valueOf(startMinute));
//            MQTTprotocol.publishData("zoneControl/endHour",String.valueOf(endHour));
//            MQTTprotocol.publishData("zoneControl/endMinute",String.valueOf(endMinute));
//
//            MQTTprotocol.publishData("zoneControl/pump","0");
//            MQTTprotocol.publishData("zoneControl/alarmPump","1");
//            MQTTprotocol.publishData("zoneControl/pH","0");
        }
        else{
            //String value_pH = intent.getStringExtra("pH");
            /* pump pH*/
            String dataControl;
            dataControl = "0 1 0"
                    + " " + String.valueOf("0")
                    + " " + String.valueOf("0")
                    + " " + String.valueOf("0")
                    + " " + String.valueOf("0");
            MQTTprotocol.publishData("zoneControl/dataControl",dataControl);

            // Publish data pump pH MQTT cloud
//            MQTTprotocol.publishData("zoneControl/startHour",String.valueOf(startHour));
//            MQTTprotocol.publishData("zoneControl/startMinute",String.valueOf(startMinute));
//            MQTTprotocol.publishData("zoneControl/endHour","0");
//            MQTTprotocol.publishData("zoneControl/endMinute","0");
//
//            MQTTprotocol.publishData("zoneControl/pump","0");
//            MQTTprotocol.publishData("zoneControl/alarmPump","0");
//            MQTTprotocol.publishData("zoneControl/pH","1");
        }
    }
}
