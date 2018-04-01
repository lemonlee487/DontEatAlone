package cyruslee487.donteatalone;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase{
    public abstract EventDao eventDao();
    private static EventDatabase mInstance;

    public static EventDatabase getDatabase(final Context context){
        if(mInstance == null){
            synchronized (EventDatabase.class){
                mInstance = Room.databaseBuilder(context.getApplicationContext()
                        ,EventDatabase.class
                        ,"event-database")
                        .build();
            }
        }
        return mInstance;
    }

}
