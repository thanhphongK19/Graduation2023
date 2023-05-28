package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.application.Task;

import java.util.List;

@Dao
public interface TaskDAO {

    @Insert
    void insertTask(Task task);

    @Query("SELECT * FROM task")
    List<Task> getListTask();

    @Query("SELECT MAX(requestCode) FROM task")
    int getRequestCode();


    @Delete
    void deleteTask(Task task);
}
