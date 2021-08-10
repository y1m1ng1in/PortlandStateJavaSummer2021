package edu.pdx.cs410J.yl6;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = { Appointment.class }, version = 1)
@TypeConverters({Converters.class})
public abstract class AppointmentRoomDatabase extends RoomDatabase {

    public abstract AppointmentDao appointmentDao();

}
