package com.example.cs4084_group_01;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.model.User;

public class ProfileViewModel extends AndroidViewModel {
    private final UserManager userManager;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    public ProfileViewModel(Application application) {
        super(application);
        userManager = UserManager.getInstance(application);
        loadCurrentUser();
    }

    private void loadCurrentUser() {
        User user = userManager.getCurrentUser();
        currentUser.setValue(user);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> updateProfile(String name, float height, float weight) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        User user = userManager.getCurrentUser();
        if (user != null) {
            user.setName(name);
            user.setHeight(height);
            user.setWeight(weight);
            userManager.saveUserProfile(user);
            currentUser.setValue(user);
            result.setValue(true);
        } else {
            result.setValue(false);
        }
        return result;
    }
} 