package Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.application.Data_zone2;

import java.util.List;

@Dao
public interface zone2DAO {

    @Insert
    void insertData(Data_zone2 zone2);

    @Query("SELECT * FROM zone2")
    List<Data_zone2> getListData();

    @Query("SELECT soil_temperature FROM zone2 ORDER BY id DESC")
    int getNewestSoilTemperature();

    @Query("SELECT soil_moisture FROM zone2 ORDER BY id DESC")
    int getNewestSoilMoisture();

    @Query("SELECT air_humidity FROM zone2 ORDER BY id DESC")
    int getNewestAirHumidity();

    @Query("SELECT air_temperature FROM zone2 ORDER BY id DESC")
    int getNewestAirTemperature();
}
