package edu.pdx.cs410J.yl6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import edu.pdx.cs410J.yl6.databinding.ActivityLogin2Binding;

public class LoginActivity extends AppCompatActivity {

    ActivityLogin2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLogin2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}