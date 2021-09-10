package edu.pdx.cs410J.yl6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import edu.pdx.cs410J.yl6.databinding.FragmentRegistrationBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    FragmentRegistrationBinding binding;
    TextInputLayout username;
    TextInputLayout password;
    TextInputLayout verifiedPassword;
    TextInputLayout email;
    TextInputLayout address;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        username = binding.editRegistrationUsername;
        password = binding.editRegistrationPassword;
        verifiedPassword = binding.editRegistrationVerifiedPassword;
        email = binding.editRegistrationEmail;
        address = binding.editRegistrationAddress;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnCreateAccount.setOnClickListener(this::registerAccount);
        binding.btnAlreadyHadAccount.setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_RegistrationFragment_to_LoginFragment);
        });
    }

    private void registerAccount(View view) {
        if (!validateForm()) {
            return;
        }

        String username = this.username.getEditText().getText().toString().trim();
        String password = this.password.getEditText().getText().toString().trim();
        String email = this.email.getEditText().getText().toString().trim();
        String address = this.address.getEditText().getText().toString().trim();

        AppointmentRestApi appointmentRestApi = RetrofitAppointmentWebService.getInstance().getAppointmentRestApi();
        Call<ApiResponseMessage> call = appointmentRestApi.register(username, password, email, address);

        call.enqueue(new Callback<ApiResponseMessage>() {
            @Override
            public void onResponse(Call<ApiResponseMessage> call, Response<ApiResponseMessage> response) {
                if (response.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(view, "User registration success!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                Gson gson = new Gson();
                ApiResponseMessage responseMessage = gson.fromJson(response.errorBody().charStream(),
                        ApiResponseMessage.class);
                String errorMessage = responseMessage.getMessage();
                if (errorMessage.contains(username)) {
                    binding.editRegistrationUsername.setErrorEnabled(true);
                    binding.editRegistrationUsername.setError(errorMessage);
                } else if (errorMessage.contains(email)) {
                    binding.editRegistrationEmail.setErrorEnabled(true);
                    binding.editRegistrationEmail.setError(errorMessage);
                } else {
                    Snackbar snackbar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseMessage> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(view, t.getMessage(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private boolean validateForm() {
        String passwordString = password.getEditText().getText().toString().trim();
        String verifiedPasswordString = verifiedPassword.getEditText().getText().toString().trim();

        boolean notEmpty;
        notEmpty = EditTextHelper.validateFieldIsNotEmpty(username, "username");
        notEmpty &= EditTextHelper.validateFieldIsNotEmpty(password, "password");
        notEmpty &= EditTextHelper.validateFieldIsNotEmpty(verifiedPassword, "confirm password");
        notEmpty &= EditTextHelper.validateFieldIsNotEmpty(email, "email");
        notEmpty &= EditTextHelper.validateFieldIsNotEmpty(address, "address");

        if (!notEmpty) {
            return false;
        }
        if (!passwordString.equals(verifiedPasswordString)) {
            verifiedPassword.setError("Please make sure your passwords match");
            return false;
        }
        return true;
    }
}
