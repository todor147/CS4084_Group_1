package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.cs4084_group_01.manager.UserManager;
import android.widget.TextView;

public class LoginActivity extends BaseActivity {
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private TextView skipLoginText;
    private CircularProgressIndicator loginProgress;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = UserManager.getInstance(this);

        if (userManager.getCurrentUser() != null) {
            startDashboardActivity();
            return;
        }

        initializeViews();
        setupInputValidation();
        setupClickListeners();
    }

    private void initializeViews() {
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        skipLoginText = findViewById(R.id.skipLoginText);
        loginProgress = findViewById(R.id.loginProgress);
    }

    private void setupInputValidation() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        };

        usernameInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);
    }

    private void validateInputs() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        boolean isValid = true;

        if (username.isEmpty()) {
            usernameLayout.setError("Username is required");
            isValid = false;
        } else {
            usernameLayout.setError(null);
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        loginButton.setEnabled(isValid);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        registerButton.setOnClickListener(v -> startRegistration());
        
        // Add click listener for skip login button
        skipLoginText.setOnClickListener(v -> {
            // Register a default test user if one doesn't exist
            if (userManager.getUserCredentials() == null) {
                userManager.registerUser("test@example.com", "password123", "Test User", 
                    success -> {
                        if (success) {
                            startDashboardActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to create test user", 
                                Toast.LENGTH_SHORT).show();
                        }
                    });
            } else {
                startDashboardActivity();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        setLoading(true);

        // Simulate network delay - remove this in production
        loginButton.postDelayed(() -> {
            if (userManager.loginUser(username, password)) {
                startDashboardActivity();
            } else {
                setLoading(false);
                showError("Invalid username or password");
            }
        }, 1000);
    }

    private void setLoading(boolean isLoading) {
        loginProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        registerButton.setEnabled(!isLoading);
        skipLoginText.setEnabled(!isLoading);
        usernameInput.setEnabled(!isLoading);
        passwordInput.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void startRegistration() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 