package com.example.cs4084_group_01;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cs4084_group_01.adapter.SleepPagerAdapter;
import com.example.cs4084_group_01.fragment.AddSleepFragment;
import com.example.cs4084_group_01.model.SleepEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SleepTrackingActivity extends BaseActivity {
    private static final String TAG = "SleepTrackingActivity";
    private static final int PAGE_ADD_SLEEP = 0;
    private static final int PAGE_HISTORY = 1;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private SleepPagerAdapter pagerAdapter;
    private AddSleepFragment addSleepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_sleep_tracking);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize fragments
        if (savedInstanceState == null) {
            addSleepFragment = new AddSleepFragment();
        }

        // Initialize ViewPager and TabLayout
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        
        // Set up adapter
        pagerAdapter = new SleepPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Register page change callback to track fragments
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "Page selected: " + position);
                
                // Store a reference to the AddSleepFragment if needed
                if (position == PAGE_ADD_SLEEP) {
                    Fragment fragment = getSupportFragmentManager()
                        .findFragmentByTag("f" + PAGE_ADD_SLEEP);
                    if (fragment instanceof AddSleepFragment) {
                        addSleepFragment = (AddSleepFragment) fragment;
                    }
                }
            }
        });
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case PAGE_ADD_SLEEP:
                    tab.setText("Add Sleep");
                    break;
                case PAGE_HISTORY:
                    tab.setText("History");
                    break;
            }
        }).attach();
        
        Log.d(TAG, "Setup complete");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_health_summary) {
            Intent intent = new Intent(this, HealthDashboardActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Helper method to edit a sleep entry
     * Called from the SleepHistoryFragment when an entry needs to be edited
     */
    public void editSleepEntry(SleepEntry entry) {
        Log.d(TAG, "editSleepEntry called for entry: " + entry.getId());
        
        // Switch to the add sleep tab
        viewPager.setCurrentItem(PAGE_ADD_SLEEP);
        
        // We need to post this to make sure the fragment is created
        viewPager.post(() -> {
            // Try to get the fragment directly from the FragmentManager first
            Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag("f" + viewPager.getCurrentItem());
                
            // If we found it, use it
            if (currentFragment instanceof AddSleepFragment) {
                Log.d(TAG, "Found AddSleepFragment via tag");
                ((AddSleepFragment) currentFragment).editSleepEntry(entry);
                return;
            }
            
            // Otherwise try our cached reference
            if (addSleepFragment != null) {
                Log.d(TAG, "Using cached AddSleepFragment");
                addSleepFragment.editSleepEntry(entry);
                return;
            }
            
            // Last resort: look through all fragments
            Log.d(TAG, "Searching through all fragments");
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                Log.d(TAG, "Found fragment: " + fragment.getClass().getSimpleName() + 
                      ", tag: " + fragment.getTag());
                if (fragment instanceof AddSleepFragment) {
                    ((AddSleepFragment) fragment).editSleepEntry(entry);
                    return;
                }
            }
            
            Log.e(TAG, "Could not find AddSleepFragment to edit entry");
        });
    }
} 