package com.example.application;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.databinding.ActivityControlBinding;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import Database.TaskDatabase;

public class ControlActivity extends AppCompatActivity implements MQTTprotocol {
    static ActivityControlBinding binding;

    /* variable for data from Node control */
    static int nutrition_valve = 0;
    static int water_valve = 0;
    static int mainTank_valve = 0;

    /* state Valve
        0 = Close
        1 = Open
    */

    static int waterLevel_mainTank = 0;
    /*  water level
        0 = empty
        1 = full
    */


    static String pH_value = " ";
    static String ORP_value = " ";

    static int firstInitialization = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        queryButton();

        if(firstInitialization == 0){

//        client.subscribeWith()
//                .topicFilter("zoneControl/dataReturn")
//                .send();
            receiveDataMQTT();
            //processData("0 0 0 0 0");
           firstInitialization = 1;
        }

//        binding.statusViewValveTank.setImageResource(R.drawable.off);
//        binding.statusViewValveWater.setImageResource(R.drawable.off);
//        binding.statusViewValveNutrition.setImageResource(R.drawable.off);

        ActionBar actionBar  = getSupportActionBar();
        actionBar.setTitle("Điều khiển");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    void queryButton()
    {

        binding.switchWater.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(binding.switchWater.isChecked()){
                            String datacontrol = "1 0 0 0 0 0 0";
                            MQTTprotocol.publishData("zoneControl/dataControl",datacontrol);
                            binding.statusPump.setText("Bật");
                        }
                        else{
                            String datacontrol = "0 0 0 0 0 0 0";
                            MQTTprotocol.publishData("zoneControl/dataControl",datacontrol);
                            binding.statusPump.setText("Tắt");
                        }
                    }
                }
        );


        binding.buttonAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ControlActivity.this,alarmList.class);
                startActivity(intent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void receiveDataMQTT()
    {
        final String[] val = new String[1];
        final CharBuffer[] value = new CharBuffer[1];

        String s1 = "zoneControl/dataReturn";
        //String s2 = "zoneSensor2/data";
        //String s3 = "zoneControl/status";

        MQTTprotocol.client.toAsync().publishes(ALL, publish ->{
            val[0] = publish.getTopic().toString();
            value[0] = UTF_8.decode(ByteBuffer.wrap(publish.getPayloadAsBytes()));

            if(s1.equals(val[0])) {

                String string = String.valueOf(value[0]);
                /* String words for data */
                String[] words = string.split("\\s");
                for (int i = 0; i < 5; i++) {
                    System.out.println(words[i]);
                    if (i == 1) {
                        nutrition_valve = Integer.parseInt(words[i]);
                        if(nutrition_valve == 1){
                           binding.statusValveNutrition.setText("Bật");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.statusViewValveNutrition.setImageResource(R.drawable.on);
                                }
                            });
                        }
                        else{
                            binding.statusValveNutrition.setText("Tắt");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.statusViewValveNutrition.setImageResource(R.drawable.off);

                                }
                            });
                        }
                    }
                    else if (i == 0){
                        water_valve = Integer.parseInt(words[i]);
                        if(water_valve == 1){
                           binding.statusValveWater.setText("Bật");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.statusViewValveWater.setImageResource(R.drawable.on);

                                }
                            });
                        }
                        else{
                            binding.statusValveWater.setText("Tắt");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.statusViewValveWater.setImageResource(R.drawable.off);

                                }
                            });
                        }
                    }
                    else if (i == 2){
                        mainTank_valve = Integer.parseInt(words[i]);
                        if(mainTank_valve == 1){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.statusViewValveTank.setImageResource(R.drawable.on);
                                    binding.statusValveTank.setText("Bật");
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.statusViewValveTank.setImageResource(R.drawable.off);
                                    binding.statusValveTank.setText("Tắt");
                                }
                            });
                        }
                    }
                    else if (i == 3){
                        waterLevel_mainTank = Integer.parseInt(words[i]);

                        if(waterLevel_mainTank == 1){
                            binding.waterLevel.setText("Có nước");
                        }
                        else{
                           binding.waterLevel.setText("Không có nước");
                        }
                    }

                }
            }

        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void processData(String string) {

    }
}
