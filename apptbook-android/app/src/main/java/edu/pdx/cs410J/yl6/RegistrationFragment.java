package edu.pdx.cs410J.yl6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import edu.pdx.cs410J.yl6.databinding.FragmentRegistrationBinding;

public class RegistrationFragment extends Fragment {

    FragmentRegistrationBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAlreadyHadAccount.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_RegistrationFragment_to_LoginFragment);
        });

        binding.btnCreateAccount.setOnClickListener(view1 -> {
            // if creation success
            NavHostFragment.findNavController(this).navigate(R.id.action_RegistrationFragment_to_LoginFragment);
        });
    }
}
