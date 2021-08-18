package edu.pdx.cs410J.yl6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class AppointmentBookAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Appointment> appointments;

    public AppointmentBookAdaptor() {
        this.appointments = null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 2) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_appointment, parent, false);
            return new AppointmentViewHolder(view);
        }
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_appointment_not_found, parent, false);
            return new PromptViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_search_init, parent, false);
        return new PromptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = holder.getItemViewType();
        if (type == 2) {
            AppointmentViewHolder appointmentHolder = (AppointmentViewHolder) holder;
            Appointment appointment = appointments.get(position);
            appointmentHolder.getOwnerTextView().setText(appointment.getOwner());
            appointmentHolder.getBeginDateTimeTextView().setText(appointment.getBeginTimeString());
            appointmentHolder.getEndDateTimeTextView().setText(appointment.getEndTimeString());
            appointmentHolder.getDescriptionTextView().setText(appointment.getDescription());

            Date begin = appointment.getBegin();
            Date end = appointment.getEnd();
            String durationText;
            long duration = (int) ((end.getTime() - begin.getTime()) / 60000);
            if (duration <= 1) {
                durationText = duration + " minute";
            } else {
                durationText = duration + " minutes";
            }

            appointmentHolder.getDurationTextView().setText(durationText);
        }
    }

    @Override
    public int getItemCount() {
        if (appointments == null || appointments.size() == 0) {
            return 1;
        }
        return appointments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (appointments == null) {
            return 0;
        }
        if (appointments.isEmpty()) {
            return 1;
        }
        return 2;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView ownerTextView;
        private final TextView descriptionTextView;
        private final TextView beginDateTimeTextView;
        private final TextView endDateTimeTextView;
        private final TextView durationTextView;

        public AppointmentViewHolder(View view) {
            super(view);
            ownerTextView = view.findViewById(R.id.textOwner);
            descriptionTextView = view.findViewById(R.id.textDesc);
            beginDateTimeTextView = view.findViewById(R.id.textBegin);
            endDateTimeTextView = view.findViewById(R.id.textEnd);
            durationTextView = view.findViewById(R.id.textDuration);
        }

        public TextView getOwnerTextView() {
            return ownerTextView;
        }

        public TextView getDescriptionTextView() {
            return descriptionTextView;
        }

        public TextView getBeginDateTimeTextView() {
            return beginDateTimeTextView;
        }

        public TextView getEndDateTimeTextView() {
            return endDateTimeTextView;
        }

        public TextView getDurationTextView() {
            return durationTextView;
        }

    }

    public static class PromptViewHolder extends RecyclerView.ViewHolder {

        public PromptViewHolder(View view) {
            super(view);
        }
    }

}

