package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.application.Data_zone1;

import java.util.List;

@Dao
public interface zone1DAO {

    @Insert
    void insertData(Data_zone1 zone1);

    @Query("SELECT * FROM zone1")
    List<Data_zone1> getListData();

    @Query("SELECT soil_temperature FROM zone1 ORDER BY id DESC")
    int getNewestSoilTemperature();

    @Query("SELECT soil_moisture FROM zone1 ORDER BY id DESC")
    int getNewestSoilMoisture();

    @Query("SELECT air_humidity FROM zone1 ORDER BY id DESC")
    int getNewestAirHumidity();

    @Query("SELECT air_temperature FROM zone1 ORDER BY id DESC")
    int getNewestAirTemperature();
}
