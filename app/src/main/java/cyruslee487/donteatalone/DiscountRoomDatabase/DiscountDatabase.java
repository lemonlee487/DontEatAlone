package cyruslee487.donteatalone.DiscountRoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import cyruslee487.donteatalone.EventRoomDatabase.EventDatabase;

@Database(entities = {Discount.class}, version = 1)
public abstract class DiscountDatabase extends RoomDatabase{
    public abstract DiscountDao discountDao();
    private static DiscountDatabase mInstance;

    public static DiscountDatabase getDatabase(final Context context){
        if(mInstance == null){
            synchronized (EventDatabase.class){
                mInstance = Room.databaseBuilder(context.getApplicationContext()
                        ,DiscountDatabase.class
                        ,"discount-database")
                        .build();
            }
        }
        return mInstance;
    }

}
