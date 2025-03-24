package com.example.cs4084_group_01;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.example.cs4084_group_01.manager.UserManager;

public class RegisterDialogFragment extends DialogFragment {
    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private UserManager userManager;
    private RegistrationListener registrationListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_register_dialog, null);

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext());

        // Initialize views
        usernameInput = view.findViewById(R.id.usernameInput);
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);

        builder.setView(view)
                .setTitle("Register")
                .setPositiveButton("Register", (dialog, id) -> handleRegister())
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }

    private void handleRegister() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (validateInput(username, email, password)) {
            userManager.registerUser(email, password, username, success -> {
                if (registrationListener != null) {
                    registrationListener.onRegistrationComplete(success);
                }
                if (success) {
                    Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
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

    public void setRegistrationListener(RegistrationListener registrationListener) {
        this.registrationListener = registrationListener;
    }

    public interface RegistrationListener {
        void onRegistrationComplete(boolean success);
    }
} 