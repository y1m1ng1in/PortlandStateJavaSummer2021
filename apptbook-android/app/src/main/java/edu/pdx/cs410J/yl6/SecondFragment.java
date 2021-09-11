package edu.pdx.cs410J.yl6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.pdx.cs410J.yl6.databinding.FragmentSecondBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private AppointmentBookAdaptor adaptor;
    private AppointmentBookViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        SingletonDataRepository repository = SingletonDataRepository.getInstance(getActivity().getApplicationContext());
        ViewModelProvider.Factory viewModelFactory =
                new AppointmentBookViewModel.AppointmentBookViewModelFactory(repository.repository);
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(AppointmentBookViewModel.class);

        adaptor = new AppointmentBookAdaptor();
        viewModel.getAllAppointments();
        viewModel.getSearchedResults().observe(getViewLifecycleOwner(), list -> {
            adaptor.setAppointments(list);
        });

        binding.searchFilter.setVisibility(View.GONE);
        binding.cardSearchAppointmentMessageBanner.setVisibility(View.GONE);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView appointmentListView = view.findViewById(R.id.listAppointments);
        appointmentListView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentListView.setAdapter(adaptor);

        binding.fab.setOnClickListener(view1 -> {
            viewModel.clearInDisplayAppointments();
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

        binding.searchViewForAppointmentByOwner.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.btnToggleSearchFilter.addOnButtonCheckedListener((toggleButton, checkedId, isChecked) -> {
            if (isChecked) {
                binding.searchFilter.setVisibility(View.VISIBLE);
            } else {
                binding.searchFilter.setVisibility(View.GONE);
            }
        });

        binding.btnClearFilter.setOnClickListener(view1 -> clearFilter());

        binding.searchAppointmentMessageAction1.setOnClickListener(view1 -> {
            clearFilter();
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.GONE);
        });
        binding.searchAppointmentMessageAction2.setOnClickListener(
                view1 -> binding.cardSearchAppointmentMessageBanner.setVisibility(View.GONE));

        EditTextHelper.getPickersForDateIntervalSelection(
                getActivity().getSupportFragmentManager(), "filter_begin_interval",
                binding.editFilterBeginTimeLowerBoundDate, binding.editFilterBeginTimeLowerBoundTime,
                binding.editFilterBeginTimeUpperBoundDate, binding.editFilterBeginTimeUpperBoundTime);

    }

    private void clearFilter() {
        binding.editFilterBeginTimeLowerBoundDate.getText().clear();
        binding.editFilterBeginTimeLowerBoundTime.getText().clear();
        binding.editFilterBeginTimeUpperBoundDate.getText().clear();
        binding.editFilterBeginTimeUpperBoundTime.getText().clear();
    }

    private void applyFilter(
            String owner
    ) {
        String lowerBoundDate = binding.editFilterBeginTimeLowerBoundDate.getText().toString();
        String lowerBoundTime = binding.editFilterBeginTimeLowerBoundTime.getText().toString();
        String upperBoundDate = binding.editFilterBeginTimeUpperBoundDate.getText().toString();
        String upperBoundTime = binding.editFilterBeginTimeUpperBoundTime.getText().toString();

        if (lowerBoundDate.equals("") && lowerBoundTime.equals("") &&
                upperBoundDate.equals("") && upperBoundTime.equals("")) {
//            viewModel.searchAllAppointmentsByOwner(owner);
            viewModel.getAllAppointments(); // simulate refresh
            return;
        }

        if (lowerBoundDate.equals("")) {
            binding.searchAppointmentMessageBannerText.setText(
                    "The date field for lower-bound begin time searching interval should not be empty.");
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return;
        }
        if (lowerBoundTime.equals("")) {
            binding.searchAppointmentMessageBannerText.setText(
                    "The time field for lower-bound begin time searching interval should not be empty.");
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return;
        }
        if (upperBoundDate.equals("")) {
            binding.searchAppointmentMessageBannerText.setText(
                    "The date for upper-bound begin time searching interval should not be empty.");
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return;
        }
        if (upperBoundTime.equals("")) {
            binding.searchAppointmentMessageBannerText.setText(
                    "The date for upper-bound begin time searching interval should not be empty.");
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return;
        }

        String begin = lowerBoundDate + " " + lowerBoundTime;
        String end = upperBoundDate + " " + upperBoundTime;
        Date beginDate = parseDate(begin);
        Date endDate = parseDate(end);
        if (!beginDate.before(endDate)) {
            binding.searchAppointmentMessageBannerText.setText(
                    "The lower-bound of searching interval should earlier than upper-bound.");
            binding.cardSearchAppointmentMessageBanner.setVisibility(View.VISIBLE);
            return;
        }
        viewModel.searchAppointmentsBetweenIntervalByOwner(owner, beginDate, endDate);

        return;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Date parseDate(String s) {
        DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
        df.setLenient(false);
        try {
            return df.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

}