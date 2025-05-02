package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.cs4084_group_01.manager.UserManager;

public class RegisterActivity extends BaseActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton registerButton;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UserManager
        userManager = UserManager.getInstance(this);

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);

        // Set up click listener
        registerButton.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (validateInput(username, email, password)) {
            userManager.registerUser(email, password, username, success -> {
                if (success) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInput(String username, String email, String password) {
        if (username.isEmpty()) {
            usernameInput.setError("Username is required");
            return false;
        }

        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email address");
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }
} 