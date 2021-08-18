package edu.pdx.cs410J.yl6;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.List;

public class AppointmentBookViewModel extends ViewModel {

    private final AppointmentBookRepository storage;
    private final MutableLiveData<List<Appointment>> appointmentInDisplay = new MutableLiveData<>();

    public AppointmentBookViewModel(AppointmentBookRepository storage) {
        this.storage = storage;
    }

    public void clearInDisplayAppointments() {
        appointmentInDisplay.postValue(null);
    }

    public void searchAllAppointmentsByOwner(String owner) {
        storage.getAllAppointmentsByOwner(owner, appointmentInDisplay::postValue);
    }

    public void searchAppointmentsBetweenIntervalByOwner(String owner, Date start, Date end) {
        storage.getAppointmentsByOwnerWithBeginInterval(owner, start, end,
                appointmentInDisplay::postValue);
    }

    public void addAppointment(Appointment appointment) {
        storage.insertAppointmentWithOwner(appointment);
    }

    public LiveData<List<Appointment>> getSearchedResults() {
        return appointmentInDisplay;
    }

    public static class AppointmentBookViewModelFactory implements ViewModelProvider.Factory {
        private final AppointmentBookRepository storage;

        public AppointmentBookViewModelFactory(AppointmentBookRepository storage) {
            this.storage = storage;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AppointmentBookViewModel.class)) {
                return (T) new AppointmentBookViewModel(storage);
            }
            throw new IllegalArgumentException(
                    "cannot create AppointmentBookViewModel due to bad argument");
        }
    }

}
