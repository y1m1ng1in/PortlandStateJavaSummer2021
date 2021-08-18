package edu.pdx.cs410J.yl6;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppointmentBookRepository {

    private final AppointmentRoomDatabase roomDatabase;
    private final AppointmentDao appointmentDao;
    private final Executor localRoomExecutor;
    private final Handler resultHandler;

    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }

    public AppointmentBookRepository(AppointmentRoomDatabase roomDatabase, Handler resultHandler) {
        this.roomDatabase = roomDatabase;
        appointmentDao = this.roomDatabase.appointmentDao();
        localRoomExecutor = Executors.newSingleThreadExecutor();
        this.resultHandler = resultHandler;
    }

    public void getAllAppointmentsByOwner(String owner, RepositoryCallback<List<Appointment>> resultCallback) {
        localRoomExecutor.execute(() -> {
            List<Appointment> listOfAppointments = appointmentDao.getAllAppointmentByOwner(owner);
            Collections.sort(listOfAppointments);
            resultHandler.post(() -> resultCallback.onComplete(listOfAppointments));
        });
    }

    public void getAppointmentsByOwnerWithBeginInterval(
            String owner,
            Date from,
            Date to,
            RepositoryCallback<List<Appointment>> resultCallback
    ) {
        localRoomExecutor.execute(() -> {
            List<Appointment> listOfAppointments =
                    appointmentDao.getAppointmentsByOwnerWithBeginInterval(owner, from, to);
            Collections.sort(listOfAppointments);
            resultHandler.post(() -> resultCallback.onComplete(listOfAppointments));
        });
    }

    public void insertAppointmentWithOwner(Appointment appointment) {
        localRoomExecutor.execute(() -> appointmentDao.insertAppointment(appointment));
    }
}
