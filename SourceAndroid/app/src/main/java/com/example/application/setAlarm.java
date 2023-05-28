package com.example.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.application.databinding.SetAlarmBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

import Database.TaskDAO;
import Database.TaskDatabase;

public class setAlarm extends AppCompatActivity
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference requestCodeDB = database.getReference("requestCode");

    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    SetAlarmBinding binding;

    // variable for set Alarm
    int monday = 0;
    int tuesday = 0;
    int thursday = 0;
    int wednesday = 0;
    int friday = 0 ;
    int saturday = 0;
    int sunday = 0;
    static int onlyone = 0;
    static int requestCode = 0;

    /* variable for save RoomDabtabase*/
    int hourDatabase = 0;
    int minuteDatabase = 0;
    String dayDatabase = "";
    int timePumpDatabase = 0;

    String pHValueDatabase = "" ;
    int rqCodeDatabase = 0;
    int numberOfDayDatabase = 0;


    // Variable for choose mode pump
    int H20_mode = 0 ;
    int Nutrition_mode = 0;

    static int timePlay = 0;
    static String value_pH = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = SetAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar  = getSupportActionBar();
        actionBar.setTitle("Hẹn giờ");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        queryDateButton();
        queryFunctionButton();



        //choose pump H20 or pump pH
        binding.text10.setVisibility(View.INVISIBLE);
        setViewEditText();
        readRequestCode();

    }
    void readRequestCode()
    {
        /* read request code in roomDatabase*/
        //requestCode = TaskDatabase.getInstance(setAlarm.this).taskDAO().getRequestCode();
        //Toast.makeText(getApplicationContext(),requestCode,Toast.LENGTH_SHORT).show();

        /*  read requestCode in firebase */
        requestCodeDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String request = Objects.requireNonNull(snapshot.getValue()).toString();
                    requestCode = Integer.parseInt(request);
                    //requestCode++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void queryDateButton()
    {

        binding.monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(monday == 1){
                    binding.monday.setBackgroundResource(R.drawable.rectangle_3);
                    monday = 0;
                }
                else{
                    binding.monday.setBackgroundResource(R.drawable.rectangle_2);
                    monday = 1;
                }
            }
        });
        binding.tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tuesday == 1){
                    binding.tuesday.setBackgroundResource(R.drawable.rectangle_3);
                    tuesday = 0;
                }
                else{
                    binding.tuesday.setBackgroundResource(R.drawable.rectangle_2);
                    tuesday = 1;
                }
            }
        });
        binding.wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wednesday == 1){
                    binding.wednesday.setBackgroundResource(R.drawable.rectangle_3);
                    wednesday = 0;
                }
                else{
                    binding.wednesday.setBackgroundResource(R.drawable.rectangle_2);
                    wednesday = 1;
                }
            }
        });

        binding.thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thursday == 1){
                    binding.thursday.setBackgroundResource(R.drawable.rectangle_3);
                    thursday = 0;
                }
                else{
                    binding.thursday.setBackgroundResource(R.drawable.rectangle_2);
                    thursday = 1;
                }
            }
        });
        binding.friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friday == 1){
                    binding.friday.setBackgroundResource(R.drawable.rectangle_3);
                    friday = 0;
                }
                else{
                    binding.friday.setBackgroundResource(R.drawable.rectangle_2);
                    friday = 1;
                }
            }
        });
        binding.saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saturday == 1){
                    binding.saturday.setBackgroundResource(R.drawable.rectangle_3);
                    saturday = 0;
                }
                else{
                    binding.saturday.setBackgroundResource(R.drawable.rectangle_2);
                    saturday = 1;
                }
            }
        });
        binding.sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sunday == 1){
                    binding.sunday.setBackgroundResource(R.drawable.rectangle_3);
                    sunday = 0;
                }
                else{
                    binding.sunday.setBackgroundResource(R.drawable.rectangle_2);
                    sunday = 1;
                }
            }
        });

    }

    private void queryFunctionButton()
    {
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(setAlarm.this,alarmList.class);
                startActivity(intent);
            }
        });

        binding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(queryInternet.isNetworkAvailable(getApplicationContext())){
                    if(queryEditText()==1){
                        sheduleNotification();
                    }
                //}
                //else{
                    //Toast.makeText(getApplicationContext(),"Không có kết nối mạng",Toast.LENGTH_SHORT).show();
                //}

            }
        });
    }

    void setViewEditText()
    {
        binding.pumpNutrition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (binding.pumpNutrition.isChecked()){
                    //binding.textPH.setVisibility(View.VISIBLE);
                    Nutrition_mode = 1;
                }
                else{
                    //binding.textPH.setVisibility(View.INVISIBLE);
                    Nutrition_mode = 0;
                }
            }
        });

        binding.pumpWater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (binding.pumpWater.isChecked()){
                    binding.text10.setVisibility(View.VISIBLE);
                    H20_mode = 1;
                }
                else{
                    binding.text10.setVisibility(View.INVISIBLE);
                    H20_mode = 0;

                }
            }
        });

    }
//
    private int queryEditText(){
        int flag = 0;
        if(H20_mode == 1){
            String timeP = binding.textH20.getText().toString();
             timePlay = Integer.parseInt(timeP);

            if(timePlay > 60 || timePlay <= 0){
                Toast.makeText(getApplicationContext(),"Giá trị hợp lệ từ 1 đến 60",Toast.LENGTH_LONG).show();
                flag = 0;
            }
            else{
                Toast.makeText(getApplicationContext(),"Đặt chuông báo thành công",Toast.LENGTH_LONG).show();
                flag = 1;
            }
        }
        else if(Nutrition_mode == 1){
            flag = 1;
        }

        return flag;
    }

    private void sheduleNotification()
    {
        clear_variableDatabase();
        if((monday+tuesday+wednesday+thursday+friday+saturday+sunday) >=1 ){
            if(monday == 1){
                getTime(2);
                dayDatabase = "2 ";
                numberOfDayDatabase++;
            }
            if(tuesday == 1){
                getTime(3);
                dayDatabase = dayDatabase + "3 ";
                numberOfDayDatabase++;
            }
            if(wednesday == 1){
                getTime(4);
                dayDatabase = dayDatabase + "4 ";
                numberOfDayDatabase++;
            }
            if(thursday == 1){
                getTime(5);
                dayDatabase = dayDatabase + "5 ";
                numberOfDayDatabase++;
            }
            if(friday == 1){
                getTime(6);
                dayDatabase = dayDatabase + "6 ";
                numberOfDayDatabase++;
            }
            if(saturday == 1){
                getTime(7);
                dayDatabase = dayDatabase + "7 ";
                numberOfDayDatabase++;
            }
            if(sunday == 1){
                getTime(1);
                dayDatabase = dayDatabase + "CN";
                numberOfDayDatabase++;
            }
        }
        else{
            getTime(0);
            numberOfDayDatabase = 1;
        }

        /* Save Database */
        addTask(hourDatabase,minuteDatabase,dayDatabase,timePumpDatabase,rqCodeDatabase,numberOfDayDatabase);
        clear_choice();
        clear_variableDatabase();
    }


    private void getTime(int date)
    {
        if(date>0){
            if(calendar.get(Calendar.DAY_OF_WEEK)==1){
                if(date==1){
                    calendar.set(Calendar.DAY_OF_WEEK,date);
                }
                else{
                    int j = calendar.get(Calendar.WEEK_OF_MONTH);
                    calendar.set(Calendar.WEEK_OF_MONTH,++j);
                    calendar.set(Calendar.DAY_OF_WEEK,date);
                    onlyone = 1;
                }
            }
            else if(date < calendar.get(Calendar.DAY_OF_WEEK)){
                int i = calendar.get(Calendar.WEEK_OF_MONTH);
                calendar.set(Calendar.WEEK_OF_MONTH,++i);
                calendar.set(Calendar.DAY_OF_WEEK,date);
                onlyone = 1;
            }
            else if(date >= calendar.get(Calendar.DAY_OF_WEEK)){
                calendar.set(Calendar.DAY_OF_WEEK,date);
            }
            //calendar.set(Calendar.DAY_OF_MONTH,binding.datePicker.month)
        }
        else{
            int i =  calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH,i);
        }
        calendar.set(Calendar.HOUR_OF_DAY,binding.timePicker.getHour());
        calendar.set(Calendar.MINUTE,binding.timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);
        //new setAlarm(date,calendar.getTimeInMillis());


        /* Set Alarm */

        /* send data to alarm Receiver */
        Intent intent = new Intent(this,AlarmReceiver.class);
        intent.putExtra("startHour",binding.timePicker.getHour());
        intent.putExtra("startMinute",binding.timePicker.getMinute());

        if(H20_mode == 1){
            //String timeP = binding.editText.getText().toString();
            //timePlay = Integer.parseInt(timeP);
            intent.putExtra("time",timePlay);

            // pump H20 <=> mode 0
            intent.putExtra("mode",0);
        }
        if(Nutrition_mode == 1){
            //value_pH = binding.editTextpH.getText().toString();
            intent.putExtra("pH",value_pH);

            // pump pH <=> mode 1
            intent.putExtra("mode",1);
        }

        if(date > 0){
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY*7,
                    pendingIntent);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }

        if(onlyone == 1){
            int i = calendar.get(Calendar.WEEK_OF_MONTH);
            calendar.set(Calendar.WEEK_OF_MONTH,--i);
            onlyone = 0;
        }


        /* Save Room Database */
        hourDatabase = binding.timePicker.getHour();
        minuteDatabase = binding.timePicker.getMinute();

        if(Nutrition_mode == 1){
            pHValueDatabase = value_pH;
            timePumpDatabase = 0;
        }
        else{
            timePumpDatabase = timePlay;
            pHValueDatabase = "";
        }

        rqCodeDatabase = requestCode;

        requestCode++;

        /* Save request Code Firebase */
        requestCodeDB.setValue(requestCode);
        //intentArray.add(pendingIntent);
    }

    private void addTask(int hour,int minute,String day,int timeP,int request,int numberOfDay)
    {
        String min = "";
        String time = "";
        String timePump =  " ";
        if(minute < 10){
            min = "0" + minute;
            time = hour + ":" + min;
        }
        else{
            time = hour + ":" + minute;
        }

        if(timeP == 0){
            timePump =  " ";
        }
        else{
           timePump = String.valueOf(timeP) + " phút";
        }
        String dayDatabase = day;
        int requestCode = request;


        Task task = new Task(time,dayDatabase,timePump,requestCode,numberOfDay);
        TaskDatabase.getInstance(setAlarm.this).taskDAO().insertTask(task);

        Intent intent = new Intent(this,alarmList.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_task", task);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void clear_choice()
    {
        monday = 0;
        tuesday = 0;
        wednesday = 0;
        thursday = 0;
        friday = 0;
        saturday = 0;
        sunday = 0;
    }
    private void clear_variableDatabase()
    {
        hourDatabase = 0;
        minuteDatabase = 0;
        dayDatabase = "";
        timePumpDatabase = 0;
        rqCodeDatabase = 0;
        numberOfDayDatabase = 0;

    }


}
