package com.example.application;

import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.ActivityMainBinding;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.time.LocalDate;
import java.util.Calendar;

import Database.zone1Database;
import Database.zone2Database;

public class MainActivity extends AppCompatActivity implements MQTTprotocol {
    ActivityMainBinding binding;

    /* database */
    static int dataSensor1[] = {0,0,0,0,0,0,0};

    static int dataSensor2[] = {0,0,0,0,0,0,0};

    static int nodeSensor1Status = 0;
    static int nodeSensor2Status = 0;

    static int nodeControlStatus = 0;

    static int gatewayStatus = 0;

    String Gateway = "Gateway";
    String Control = "Control";
    String Sensor1 = "Sensor1";
    String Sensor2 = "Sensor2";
    static int id;

    static int showAgain = 0;


    static  int firstInitialization = 0;
    Calendar calendar;
    static int connectMQTT = 0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();

        if(connectMQTT == 1){
            binding.connect.setBackgroundResource(R.drawable.rectangle_10);
            receiveDataMQTT();
            showStatus(Gateway,gatewayStatus);
        }

        initCheckStatus();
//        if(firstInitialization == 0) {
//            firstInitialization = 1;
//            initCheckStatus();
//        }


        binding.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MQTTprotocol.setupConnectMQTT();
                subscribeTopicMQTT();
                receiveDataMQTT();
                binding.connect.setBackgroundResource(R.drawable.rectangle_10);
                connectMQTT = 1;
                gatewayStatus = 1;
                showStatus(Gateway,gatewayStatus);
            }
        });

        binding.function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FunctionActivity.class);
                startActivity(intent);
            }
        });

        clearArray(1);
        clearArray(2);


    }
    @Override
    protected void onResume() {
        super.onResume();
        if(showAgain == 1){
            saveStatusNode();
            showAgain = 0;
        }

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        showAgain = 1;

    }

    void initCheckStatus()
    {
        binding.statusGateway.setText("Đang kiểm tra...");
        binding.statusNodeControl.setText("Đang kiểm tra...");
        binding.statusNodeSenSor1.setText("Đang kiểm tra...");
        binding.statusNodeSenSor2.setText("Đang kiểm tra...");
    }

    void saveStatusNode(){
        showStatus(Gateway,gatewayStatus);
        showStatus(Control,nodeControlStatus);
        showStatus(Sensor1,nodeSensor1Status);
        showStatus(Sensor2,nodeSensor2Status);
    }

    void showStatus(String device, int status)
    {
        if(device == "Gateway"){
            if(status == 0){
                binding.statusGateway.setText("Mất kết nối");
                binding.statusGateway.setTextColor(Color.RED);
            }
            else{
                binding.statusGateway.setText("Đã kết nối");
                binding.statusGateway.setTextColor(Color.GREEN);
            }
        }
        else if(device == "Control"){
            if(status == 0){
                binding.statusNodeControl.setText("Mất kết nối");
                binding.statusNodeControl.setTextColor(Color.RED);
            }
            else{
                binding.statusNodeControl.setText("Đã kết nối");
                binding.statusNodeControl.setTextColor(Color.GREEN);
            }
        }
        else if(device == "Sensor1"){
            if(status == 0){
                binding.statusNodeSenSor1.setText("Mất kết nối");
                binding.statusNodeSenSor1.setTextColor(Color.RED);
            }
            else{
                binding.statusNodeSenSor1.setText("Đã kết nối");
                binding.statusNodeSenSor1.setTextColor(Color.GREEN);
            }
        }
        else if(device == "Sensor2"){
            if(status == 0){
                binding.statusNodeSenSor2.setText("Mất kết nối");
                binding.statusNodeSenSor2.setTextColor(Color.RED);
            }
            else{
                binding.statusNodeSenSor2.setText("Đã kết nối");
                binding.statusNodeSenSor2.setTextColor(Color.GREEN);
            }
        }

    }
    void subscribeTopicMQTT()
    {
        /* topic for Node Control */
        client.subscribeWith()
                .topicFilter("zoneControl/dataReturn")
                .send();
        ////////////////////////////////////////////


        /* topic for Node Sensor */
        // Node sensor of Zone 1
        client.subscribeWith()
                .topicFilter("zoneSensor1/data")
                .send();


        // Node sensor of Zone 2
        client.subscribeWith()
                .topicFilter("zoneSensor2/data")
                .send();
        ////////////////////////////////////////////
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void receiveDataMQTT()
    {
        final String[] val = new String[1];
        final CharBuffer[] value = new CharBuffer[1];

        String s1 = "zoneSensor1/data";
        String s2 = "zoneSensor2/data";
        String s3 = "zoneControl/dataReturn";


        MQTTprotocol.client.toAsync().publishes(ALL, publish ->{
            val[0] = publish.getTopic().toString();
            value[0] = UTF_8.decode(ByteBuffer.wrap(publish.getPayloadAsBytes()));

            if(s1.equals(val[0])){
                processData(1,String.valueOf(value[0]));
                saveDatabase(1);
                showStatus(Sensor1,nodeSensor1Status);
                clearArray(1);
            }
            else if(s2.equals(val[0])){
                processData(2,String.valueOf(value[0]));
                saveDatabase(2);
                showStatus(Sensor2,nodeSensor2Status);
                clearArray(2);
            }
            else if(s3.equals(val[0])){
                processData(0,String.valueOf(value[0]));
                showStatus(Control,nodeControlStatus);
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void processData(int zone, String string)
    {
        String time = "";
        time = String.valueOf(LocalDate.now());

        if(zone == 1){
            /* String word for time */
            String[] word = time.split("-");

            /* String words for data */
            String[] words = string.split("\\s");
            for (int i = 0; i<7; i++) {
                if(i<2){
                    dataSensor1[i] = Integer.parseInt(word[i+1]);
                }
                else{
                    dataSensor1[i] = Integer.parseInt(words[i-2]);
                }
                System.out.println(dataSensor1[i]);
            }
        }
        else if(zone == 2){
            /* String word for time */
            String[] word = time.split("-");

            /* String words for data */
            String[] words = string.split("\\s");
            for (int i = 0; i<7; i++) {
                if(i<2){
                    dataSensor2[i] = Integer.parseInt(word[i+1]);
                }
                else{
                    dataSensor2[i] = Integer.parseInt(words[i-2]);
                }
                System.out.println(dataSensor2[i]);
            }
        }
        else if(zone == 0){
            String[] words = string.split("\\s");
            nodeControlStatus = Integer.parseInt(words[4]);
        }


    }

    void saveDatabase(int zone)
    {
        if(zone == 1){
            nodeSensor1Status = dataSensor1[6];
            Data_zone1 zone1 = new Data_zone1(dataSensor1[1],dataSensor1[0],dataSensor1[3],dataSensor1[2],dataSensor1[4],dataSensor1[5]);
            zone1Database.getInstance(MainActivity.this).zone1DAO().insertData(zone1);
        }
        else{
            nodeSensor2Status = dataSensor2[6];
            Data_zone2 zone2 = new Data_zone2(dataSensor2[1],dataSensor2[0],dataSensor2[3],dataSensor2[2],dataSensor2[4],dataSensor2[5]);
            zone2Database.getInstance(MainActivity.this).zone2DAO().insertData(zone2);
        }
    }
    void clearArray(int zone)
    {
        for(int i = 0; i< 7;i++){
            if(zone == 1){
                dataSensor1[i] = 0;
            }
            else{
                dataSensor2[i] = 0;
            }
        }
    }


}
