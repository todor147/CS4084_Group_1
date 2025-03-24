package com.example.cs4084_group_01.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.User;

public class ProfileViewModel extends AndroidViewModel {
    private final UserManager userManager;
    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<Boolean> isProfileComplete;

    public ProfileViewModel(Application application) {
        super(application);
        userManager = UserManager.getInstance(application);
        currentUser = new MutableLiveData<>();
        isProfileComplete = new MutableLiveData<>();
        loadUserProfile();
    }

    private void loadUserProfile() {
        User user = userManager.getCurrentUser();
        currentUser.setValue(user);
        isProfileComplete.setValue(user != null);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> isProfileComplete() {
        return isProfileComplete;
    }

    public LiveData<Boolean> updateProfile(String name, float height, float weight) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        User user = currentUser.getValue();
        if (user != null) {
            user.setName(name);
            user.setHeight(height);
            user.setWeight(weight);
            boolean success = userManager.updateUser(user);
            result.setValue(success);
            if (success) {
                currentUser.setValue(user);
            }
        } else {
            result.setValue(false);
        }
        return result;
    }

    public LiveData<Boolean> updateUser(User user) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        if (user != null) {
            boolean success = userManager.updateUser(user);
            result.setValue(success);
            if (success) {
                currentUser.setValue(user);
            }
        } else {
            result.setValue(false);
        }
        return result;
    }

    public void clearProfile() {
        userManager.logoutUser();
        currentUser.setValue(null);
        isProfileComplete.setValue(false);
    }

    public boolean hasProfile() {
        return currentUser.getValue() != null;
    }
}