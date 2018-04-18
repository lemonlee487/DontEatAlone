package cyruslee487.donteatalone.DiscountRoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DiscountDao {
    @Query("SELECT * FROM discount")
    List<Discount> getAll();

    @Insert
    void insert(Discount...discounts);

    @Delete
    void delete(Discount discount);
}
