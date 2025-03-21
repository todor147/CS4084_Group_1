package com.example.cs4084_group_01;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.cs4084_group_01.manager.UserManager;

public class RegisterDialogFragment extends DialogFragment {

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private UserManager userManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_register, null);

        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        emailInput = view.findViewById(R.id.emailInput);

        userManager = UserManager.getInstance(requireContext());

        builder.setView(view)
                .setPositiveButton("Register", (dialog, which) -> attemptRegistration())
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }

    private void attemptRegistration() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        if (validateInput(username, password, email)) {
            if (userManager.registerUser(username, password, email)) {
                Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();

                // Log the user in automatically
                userManager.loginUser(username, password);

                // Start ProfileActivity for initial setup
                Intent intent = new Intent(requireContext(), ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Close the registration dialog and login activity
                if (getActivity() != null) {
                    getActivity().finish();
                }
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String username, String password, String email) {
        // Implement your validation logic here
        return true; // Placeholder return, actual implementation needed
    }
} 