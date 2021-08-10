package edu.pdx.cs410J.yl6;

import android.widget.EditText;

import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditTextHelper {

    public static void getPickersForDateIntervalSelection(
            FragmentManager fragmentManager,
            String tag,
            EditText beginDate,
            EditText beginTime,
            EditText endDate,
            EditText endTime
    ) {
        addDatePicker(fragmentManager, tag + "_begin_date_picker", beginDate);
        addDatePicker(fragmentManager, tag + "_end_date_picker", endDate);
        addTimePicker(fragmentManager, tag + "_begin_time_picker", beginTime);
        addTimePicker(fragmentManager, tag + "_end_time_picker", endTime);
    }


    public static void addDatePicker(FragmentManager fragmentManager, String tag, EditText editText) {
        editText.setOnClickListener(view15 -> {
            AppointmentDatePickerFragment datePickerFragment = new AppointmentDatePickerFragment(
                    (view1, year, month, day) -> editText.setText(String.format("%d/%d/%d", month + 1, day, year)));
            datePickerFragment.show(fragmentManager, tag);
        });
    }

    public static void addTimePicker(FragmentManager fragmentManager, String tag, EditText editText) {
        editText.setOnClickListener(view1 -> {
            AppointmentTimePickerFragment timePickerFragment = new AppointmentTimePickerFragment(
                    (view11, hour, minute) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("H:m");
                            Date t = sdf.parse(String.format("%d:%d", hour, minute));

                            SimpleDateFormat selectedTimeForEditText = new SimpleDateFormat("h:mm a");
                            editText.setText(selectedTimeForEditText.format(t));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
            timePickerFragment.show(fragmentManager, tag);
        });

    }
}
