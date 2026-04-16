package com.example.fastlocationtest.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.fastlocationtest.model.Address;

@Database(entities = {Address.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract AddressDao addressDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "fast_location_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}