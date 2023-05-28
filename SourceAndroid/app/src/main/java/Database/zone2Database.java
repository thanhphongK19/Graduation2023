package Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.example.application.Data_zone2;

@Database(entities = {Data_zone2.class},version = 1)
public abstract class zone2Database extends RoomDatabase
{
    private static final String DATABASE_NAME = "zone2.db";

    private static zone2Database instance;

    public static synchronized zone2Database getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),zone2Database.class,DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract zone2DAO zone2DAO();
}
