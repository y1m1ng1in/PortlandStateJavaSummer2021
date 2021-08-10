package edu.pdx.cs410J.yl6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.pdx.cs410J.yl6.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private AppointmentBookViewModel viewModel;
    private TextInputLayout textInputOwner;
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputBeginDate;
    private TextInputLayout textInputBeginTime;
    private TextInputLayout textInputEndDate;
    private TextInputLayout textInputEndTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.cardSearchAppointmentMessageBanner.setVisibility(View.GONE);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditTextHelper.getPickersForDateIntervalSelection(
                getActivity().getSupportFragmentManager(), "appointment_creation",
                binding.editAddApptBeginDate.getEditText(), binding.editAddApptBeginTime.getEditText(),
                binding.editAddApptEndDate.getEditText(), binding.editAddApptEndTime.getEditText());

        SingletonDataRepository repository = SingletonDataRepository.getInstance(getActivity().getApplicationContext());
        ViewModelProvider.Factory viewModelFactory =
                new AppointmentBookViewModel.AppointmentBookViewModelFactory(repository.repository);
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(AppointmentBookViewModel.class);

        binding.btnCreateAppt.setOnClickListener(view1 -> {
            Appointment appointment = createAppointment(view);
            if (appointment != null) {
                viewModel.addAppointment(appointment);
                Snackbar snackbar = Snackbar.make(view, "Add appointment: " + appointment.toString(),
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        binding.searchAppointmentMessageAction1.setOnClickListener(this::clearDateEditTexts);
        binding.searchAppointmentMessageAction2.setOnClickListener(
                view1 -> binding.cardSearchAppointmentMessageBanner.setVisibility(View.GONE));

        initializeTextFieldErrorMessage();
    }

    public void initializeTextFieldErrorMessage() {
        textInputOwner = binding.editAddApptOwner;
        textInputDescription = binding.editAddApptDescription;
        textInputBeginDate = binding.editAddApptBeginDate;
        textInputBeginTime = binding.editAddApptBeginTime;
        textInputEndDate = binding.editAddApptEndDate;
        textInputEndTime = binding.editAddApptEndTime;

        textInputOwner.setError(null);
        textInputDescription.setError(null);
        textInputBeginDate.setError(null);
        textInputBeginTime.setError(null);
        textInputEndDate.setError(null);
        textInputEndTime.setError(null);
    }

    public Appointment createAppointment(View view) {
        String owner = textInputOwner.getEditText().getText().toString().trim();
        String description = textInputDescription.getEditText().getText().toString().trim();
        String beginDate = textInputBeginDate.getEditText().getText().toString();
        String beginTime = textInputBeginTime.getEditText().getText().toString();
        String endDate = textInputEndDate.getEditText().getText().toString();
        String endTime = textInputEndTime.getEditText().getText().toString();

        Appointment.AppointmentBuilder appointmentBuilder = new Appointment.AppointmentBuilder();

        if (!appointmentBuilder.setOwner(owner)) {
            textInputOwner.setError(appointmentBuilder.getErrorMessage());
            return null;
        } else {
            textInputOwner.setError(null);
        }

        if (beginDate.trim().equals("")) {
            textInputBeginDate.setError("Should not be empty");
            return null;
        } else {
            textInputBeginDate.setError(null);
        }

        if (beginTime.trim().equals("")) {
            textInputBeginTime.setError("Should not be empty");
            return null;
        } else {
            textInputBeginTime.setError(null);
        }

        if (endDate.trim().equals("")) {
            textInputEndDate.setError("Should not be empty");
            return null;
        } else {
            textInputEndDate.setError(null);
        }

        if (endTime.trim().equals("")) {
            textInputEndTime.setError("Should not be empty");
            return null;
        } else {
            textInputEndTime.setError(null);
        }

        if (!appointmentBuilder.setDescription((description))) {
            textInputDescription.setError(appointmentBuilder.getErrorMessage());
            return null;
        } else {
            textInputDescription.setError(null);
        }

        String begin = beginDate + " " + beginTime;
        String end = endDate + " " + endTime;
        if (!appointmentBuilder.setBeginTime(begin)) {
            binding.searchAppointmentMessageBannerText.setText(appointmentBuilder.getErrorMessage());
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return null;
        }
        if (!appointmentBuilder.setEndTime(end)) {
            binding.searchAppointmentMessageBannerText.setText(appointmentBuilder.getErrorMessage());
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return null;
        }
        return appointmentBuilder.build();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void clearDateEditTexts(View view1) {
        binding.editAddApptBeginDate.getEditText().getText().clear();
        binding.editAddApptBeginTime.getEditText().getText().clear();
        binding.editAddApptEndDate.getEditText().getText().clear();
        binding.editAddApptEndTime.getEditText().getText().clear();
        binding.cardSearchAppointmentMessageBanner.setVisibility(View.GONE);
    }
}