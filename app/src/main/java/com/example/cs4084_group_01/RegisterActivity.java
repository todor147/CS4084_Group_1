package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.cs4084_group_01.manager.UserManager;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameInput, passwordInput, confirmPasswordInput, emailInput;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UserManager
        userManager = UserManager.getInstance(this);

        // Initialize views
        initializeViews();

        // Setup register button
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        emailInput = findViewById(R.id.emailInput);
    }

    private void attemptRegistration() {
        // Get input values
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(username, password, confirmPassword, email)) {
            return;
        }

        // Attempt registration
        if (userManager.registerUser(username, password, email)) {
            // Registration successful
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            
            // Log the user in automatically
            userManager.loginUser(username, password);
            
            // Start ProfileActivity for initial setup
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            // Registration failed
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String username, String password, String confirmPassword, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
} 