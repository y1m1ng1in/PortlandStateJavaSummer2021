package edu.pdx.cs410J.yl6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.room.Room;

public class SingletonDataRepository {

    private static SingletonDataRepository singleRepo = null;

    public AppointmentBookRepository repository;

    public AppointmentRoomDatabase roomDatabase;

    public Handler mainThreadHandler;

    private SingletonDataRepository(final Context context) {
        roomDatabase = Room.databaseBuilder(context, AppointmentRoomDatabase.class, "appointment_database").build();
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        SharedPreferences sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String loginCookie = sharedPreferences.getString("login_cookie", null);
        repository = new AppointmentBookRepository(roomDatabase, mainThreadHandler, username, loginCookie);
    }

    public static SingletonDataRepository getInstance(final Context context) {
        if (singleRepo == null) {
            synchronized (SingletonDataRepository.class) {
                if (singleRepo == null) {
                    singleRepo = new SingletonDataRepository(context);
                }
            }
        }
        return singleRepo;
    }


}
