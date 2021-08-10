package edu.pdx.cs410J.yl6;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface AppointmentDao {

    @Query("Select * FROM appointments WHERE owner = :owner")
    List<Appointment> getAllAppointmentByOwner(String owner);

    @Query("Select * from appointments where owner = :owner and `begin` >= :begin and `begin` <= :end")
    List<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, Date begin, Date end);

    @Insert
    void insertAppointment(Appointment... appointment);

}
