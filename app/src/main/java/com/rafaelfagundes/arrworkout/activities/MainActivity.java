package com.rafaelfagundes.arrworkout.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.Toast;

import com.rafaelfagundes.arrworkout.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    // Shared preferences to store workout session data
    private SharedPreferences sharedPreferences;
    // View binding to interact with the UI elements
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("WorkoutSessions", Context.MODE_PRIVATE);

        // Set an action listener on the height input field
        binding.editTextHeight.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Save the entered height to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int height = Integer.parseInt(binding.editTextHeight.getText().toString());
                editor.putInt("height", height);
                editor.apply();
                // Hide the keyboard
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Set an action listener on the weight input field
        binding.editTextWeight.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Save the entered weight to shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int weight = Integer.parseInt(binding.editTextWeight.getText().toString());
                editor.putInt("weight", weight);
                editor.apply();
                // Hide the keyboard
                hideKeyboard();
                return true;
            }
            return false;
        });

        // Set a change listener on the seek bar
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the displayed step count as the seek bar is moved
                binding.textViewNumberOfSteps.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Set a click listener on the button to start a workout session
        binding.button.setOnClickListener(view -> addWorkoutSession());
    }

    private void addWorkoutSession() {
        // Get the entered height and weight
        String heightStr = binding.editTextHeight.getText().toString();
        String weightStr = binding.editTextWeight.getText().toString();

        // Check if height and weight are entered
        if (heightStr.isEmpty() || weightStr.isBlank()) {
            Toast.makeText(this, "Please enter both weight and height", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if height is 0
        if (Integer.parseInt(heightStr) == 0) {
            Toast.makeText(this, "Height must be more than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if weight is 0
        if (Integer.parseInt(weightStr) == 0) {
            Toast.makeText(this, "Weight must be more than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if step count is 0
        if (binding.seekBar.getProgress() == 0) {
            Toast.makeText(this, "Steps must be more than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the step count, height, and weight
        int stepCount = binding.seekBar.getProgress();
        int height = Integer.parseInt(heightStr);
        int weight = Integer.parseInt(weightStr);

        // Calculate stride
        double stride = height * 0.414;

        // Calculate distance
        double distance = stride * stepCount;

        // Calculate speed based on step count
        double speed;
        if (stepCount <= 79) {
            speed = 0.9;
        } else if (stepCount <= 99) {
            speed = 1.34;
        } else {
            speed = 1.79;
        }

        // Calculate walking time
        double time = distance / speed;

        // Calculate calories burned
        double calories = time * 3.5 * weight / (200 * 60);

        // Save session details
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int sessionCount = sharedPreferences.getInt("sessionCount", 0);
        editor.putInt("stepCount_" + sessionCount, stepCount);
        editor.putInt("height_" + sessionCount, height);
        editor.putInt("weight_" + sessionCount, weight);
        editor.putFloat("caloriesBurned_" + sessionCount, (float) calories);
        editor.putInt("sessionCount", sessionCount + 1);
        editor.apply();

        // Start new activity or perform necessary actions
        Intent intent = new Intent(MainActivity.this, SessionsListActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard() {
        // Get the currently focused view
        View view = this.getCurrentFocus();
        if (view != null) {
            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}