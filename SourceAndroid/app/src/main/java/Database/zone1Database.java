package Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.application.Data_zone1;
import com.example.application.MQTTprotocol;
import android.content.Context;


@Database(entities = {Data_zone1.class},version = 1)
public abstract class zone1Database extends RoomDatabase
{
    private static final String DATABASE_NAME = "zone1.db";

    private static zone1Database instance;

    public static synchronized zone1Database getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),zone1Database.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract zone1DAO zone1DAO();
}
