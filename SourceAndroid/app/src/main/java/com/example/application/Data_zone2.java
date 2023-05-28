package com.example.application;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "zone2")
public class Data_zone2 implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int soil_temperature;
    private int soil_moisture;

    private int air_humidity;
    private int air_temperature;

    private int day;
    private int month;


    public Data_zone2(int day, int month,int soil_temperature, int soil_moisture, int air_humidity, int air_temperature) {
        this.day = day;
        this.month = month;
        this.soil_temperature = soil_temperature;
        this.soil_moisture = soil_moisture;
        this.air_humidity = air_humidity;
        this.air_temperature = air_temperature;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSoil_temperature() {
        return soil_temperature;
    }

    public void setSoil_temperature(int soil_temperature) {
        this.soil_temperature = soil_temperature;
    }

    public int getSoil_moisture() {
        return soil_moisture;
    }

    public void setSoil_moisture(int soil_moisture) {
        this.soil_moisture = soil_moisture;
    }

    public int getAir_humidity() {
        return air_humidity;
    }

    public void setAir_humidity(int air_humidity) {
        this.air_humidity = air_humidity;
    }

    public int getAir_temperature() {
        return air_temperature;
    }

    public void setAir_temperature(int air_temperature) {
        this.air_temperature = air_temperature;
    }
}
