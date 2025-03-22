package com.example.cs4084_group_01.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cs4084_group_01.model.UserProfile;
import com.example.cs4084_group_01.manager.UserManager;
import com.example.cs4084_group_01.repository.ProfileRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final ProfileRepository repository;
    private final MutableLiveData<UserProfile> profileLiveData;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final UserManager userManager;

    public ProfileViewModel(Application application) {
        super(application);
        repository = new ProfileRepository(application);
        profileLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        userManager = UserManager.getInstance(application);
        loadProfile();
    }

    public void loadProfile() {
        // Get profile from UserManager for user-specific profiles
        String currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            UserProfile profile = userManager.getUserProfile(currentUser);
            profileLiveData.setValue(profile);
        }
    }

    public void saveProfile(int age, float height, float weight, String gender, String activityLevel) {
        UserProfile profile = new UserProfile();
        profile.setAge(age);
        profile.setHeight(height);
        profile.setWeight(weight);
        profile.setGender(gender);
        profile.setActivityLevel(activityLevel);

        // Save to both repository and UserManager
        repository.saveProfile(profile);
        String currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            userManager.saveUserProfile(currentUser, profile);
        }
        profileLiveData.setValue(profile);
    }

    public LiveData<UserProfile> getProfileLiveData() {
        return profileLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void deleteProfile() {
        isLoading.setValue(true);
        repository.deleteProfile();
        String currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            userManager.saveUserProfile(currentUser, null);
        }
        profileLiveData.setValue(null);
        isLoading.setValue(false);
    }

    public boolean hasProfile() {
        String currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            return userManager.getUserProfile(currentUser) != null;
        }
        return false;
    }
} 