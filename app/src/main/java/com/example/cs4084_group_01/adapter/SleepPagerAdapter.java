package com.example.cs4084_group_01.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cs4084_group_01.fragment.AddSleepFragment;
import com.example.cs4084_group_01.fragment.SleepHistoryFragment;

public class SleepPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 2;
    private static final int PAGE_ADD_SLEEP = 0;
    private static final int PAGE_HISTORY = 1;

    public SleepPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case PAGE_ADD_SLEEP:
                return new AddSleepFragment();
            case PAGE_HISTORY:
                return new SleepHistoryFragment();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
} 