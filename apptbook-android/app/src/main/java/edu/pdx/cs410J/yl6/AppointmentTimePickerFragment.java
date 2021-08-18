package edu.pdx.cs410J.yl6;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class AppointmentTimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener callback;

    public AppointmentTimePickerFragment(TimePickerDialog.OnTimeSetListener callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), callback, hour, minute, false);
    }
}
