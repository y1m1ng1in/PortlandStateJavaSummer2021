package edu.pdx.cs410J.yl6;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentBookRepository {

    private final AppointmentRoomDatabase roomDatabase;
    private final AppointmentDao appointmentDao;
    private final AppointmentRestApi webService;
    private final Executor localRoomExecutor;
    private final Handler resultHandler;
    private final String currentUser;

    public interface RepositoryCallback<T> {
        void onComplete(T result);
    }

    public AppointmentBookRepository(AppointmentRoomDatabase roomDatabase, Handler resultHandler,
                                     String username, String loginCookie) {
        this.roomDatabase = roomDatabase;
        appointmentDao = this.roomDatabase.appointmentDao();

        RetrofitAppointmentWebService retrofit = RetrofitAppointmentWebService.getInstance();
        retrofit.setAuthenticationHeader(loginCookie);
        webService = retrofit.getAuthenticatedAppointmentRestApi();

        localRoomExecutor = Executors.newSingleThreadExecutor();
        this.resultHandler = resultHandler;
        currentUser = username;
    }

    public void getAllAppointments(RepositoryCallback<List<Appointment>> resultCallback) {
        Call<AppointmentBook<Appointment>> call = webService.getAllAppointmentsByUsername(currentUser);
        call.enqueue(new Callback<AppointmentBook<Appointment>>() {
            @Override
            public void onResponse(Call<AppointmentBook<Appointment>> call, Response<AppointmentBook<Appointment>> response) {
                if (response.isSuccessful()) {
                    AppointmentBook<Appointment> appointmentBook = response.body();
                    List<Appointment> listOfAppointments = new ArrayList<>(appointmentBook.getAppointments());
                    resultHandler.post(() -> resultCallback.onComplete(listOfAppointments));
                } else {
                    Log.d("when get all appointments from retrofit222   ", response.body().toString());
                    resultHandler.post(() -> resultCallback.onComplete(new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<AppointmentBook<Appointment>> call, Throwable t) {
                Log.d("when get all appointments from retrofit111", t.getCause().toString());
            }
        });
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
