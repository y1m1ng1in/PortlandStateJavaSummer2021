package edu.pdx.cs410J.yl6;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import edu.pdx.cs410J.yl6.databinding.FragmentLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegistration.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_LoginFragment_to_RegistrationFragment);
        });

        binding.btnLogin.setOnClickListener(view1 -> {
            // if login success
            String username = binding.editLoginUsername.getEditText().getText().toString().trim();
            String password = binding.editLoginPassword.getEditText().getText().toString().trim();

            AppointmentRestApi appointmentRestApi = RetrofitAppointmentWebService.getInstance().getAppointmentRestApi();
            Call<ApiResponseMessage> call = appointmentRestApi.login(username, password);

            call.enqueue(new Callback<ApiResponseMessage>() {
                @Override
                public void onResponse(Call<ApiResponseMessage> call, Response<ApiResponseMessage> response) {
                    if (!response.isSuccessful()) {
                        ApiResponseMessage responseMessage = response.body();
                        if (responseMessage.getStatus() == 403) {
                            // unregistered user
                            binding.textLoginMessage.setText("Unregistered user");
                        } else if (responseMessage.getStatus() == 401) {
                            // wrong password
                            binding.textLoginMessage.setText("error password");
                        } else {
                            // server internal error
                            binding.textLoginMessage.setText(responseMessage.getMessage());
                        }
                    } else {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseMessage> call, Throwable t) {
                    binding.textLoginMessage.setText(t.getMessage());
                }
            });
        });
    }
}
