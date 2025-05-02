# Health and Wellness Tracker - Technical Specification

## Overview

This document outlines the technical specifications for developing a Health and Wellness Tracker
Android application written in Java. The application aims to help users monitor various aspects of
their health including steps, water intake, sleep, mood, workouts, and meals.

## Target Platform

- Android SDK 21+ (Android 5.0 Lollipop and above)
- Java (not Kotlin)
- Android Studio development environment

## Architecture

The application should follow the MVVM (Model-View-ViewModel) architecture pattern for clean
separation of concerns:

- **Model**: Data classes and repositories for storing and retrieving user health data
- **View**: Activities and Fragments for UI representation
- **ViewModel**: Classes to handle business logic and communication between View and Model

## Data Storage

- All data should be stored locally using GSON for serialization/deserialization
- SharedPreferences should be used for user settings and profile information
- Consider implementing a custom DataManager class to handle all data operations

## Dependencies

- AndroidX libraries for UI components
- GSON (com.google.code.gson:gson:2.10.1) for JSON serialization
- Material Design components for consistent UI
- Android Sensor APIs for step counting

## Features Specification

### 1. Basic User Profile

**Description**: Simple profile system to store essential user metrics and basic health goals.

**Technical Requirements**:

- Create a `UserProfile` data class to store user information (age, height, weight, goals)
- Implement GSON serialization/deserialization for profile data
- Create a `ProfileRepository` class to handle profile data operations
- Design UI with EditText fields for user input with appropriate input validation
- Implement `ProfileViewModel` to manage the data flow between UI and repository

**UI Components**:

- Profile creation/edit form with Material Design TextInputLayout
- Profile dashboard with user metrics displayed as cards
- Form validation with appropriate error messages

### 2. Simple Step Counter

**Description**: Basic step counter using device's built-in sensors with goal setting and history.

**Technical Requirements**:

- Implement a `StepCounterService` extending `Service` to run in the background
- Use `SensorManager` and `Sensor.TYPE_STEP_DETECTOR` or `Sensor.TYPE_STEP_COUNTER`
- Create a `StepData` model class to store daily steps
- Implement a `StepRepository` for data persistence using GSON
- Design a `StepViewModel` to manage counter data and goals

**Permission Requirements**:

- `android.permission.ACTIVITY_RECOGNITION` for step counting
- Handle runtime permissions for Android 10+ devices

**UI Components**:

- Circular progress indicator showing steps vs goal
- History view with RecyclerView showing past day counts
- Goal setting dialog with number input

### 3. Water Intake Tracker

**Description**: Counter to log daily water consumption with goal setting functionality.

**Technical Requirements**:

- Create a `WaterIntake` data class to store daily consumption data
- Implement a `WaterRepository` for data operations with GSON
- Design a `WaterViewModel` to manage water intake tracking logic
- Create utility methods to calculate daily water requirements based on user profile

**UI Components**:

- Counter display with increment/decrement buttons
- Water bottle visualization that fills as consumption increases
- Daily history view with RecyclerView
- Water goal setting dialog

### 4. Sleep Logger

**Description**: Interface to manually log sleep duration and quality with history view.

**Technical Requirements**:

- Create a `SleepEntry` data class to store sleep data (start time, end time, quality rating)
- Implement a `SleepRepository` for data operations with GSON
- Design a `SleepViewModel` to manage sleep logging logic
- Create time calculation utilities to determine sleep duration

**UI Components**:

- Time picker dialogs for sleep start and end times
- Rating bar (1-5) for sleep quality
- Weekly sleep summary with chart visualization
- History view with RecyclerView showing past sleep entries

### 5. Mood Tracker

**Description**: Daily mood check-in with basic emotion options and notes.

**Technical Requirements**:

- Create a `MoodEntry` data class to store mood data (mood type, timestamp, notes)
- Define an enum for mood types (e.g., HAPPY, SAD, NEUTRAL, ANXIOUS, ENERGETIC)
- Implement a `MoodRepository` for data operations with GSON
- Design a `MoodViewModel` to manage mood tracking logic
- Implement notification for daily mood check-in reminders

**UI Components**:

- Grid of mood icons for selection
- Optional notes input field
- Calendar view showing mood history with color coding
- Monthly mood summary statistics

### 6. Basic Workout Logger

**Description**: Form to log workout type, duration, and intensity with history.

**Technical Requirements**:

- Create a `WorkoutEntry` data class to store workout data
- Define enums for workout types and intensity levels
- Implement a `WorkoutRepository` for data operations with GSON
- Design a `WorkoutViewModel` to manage workout logging logic

**UI Components**:

- Workout type selection spinner or radio buttons
- Duration input with number picker
- Intensity selection with slider or radio buttons
- History list with RecyclerView
- Summary statistics view

### 7. Simple Meal Tracker

**Description**: Basic meal logging by type with food group categorization.

**Technical Requirements**:

- Create a `MealEntry` data class to store meal data
- Define enums for meal types (BREAKFAST, LUNCH, DINNER, SNACK)
- Implement food group categorization system
- Design a `MealRepository` for data operations with GSON
- Create a `MealViewModel` to manage meal tracking logic

**UI Components**:

- Meal type selection tabs
- Food group selection checklist
- Optional meal description field
- Daily nutrition summary with food group distribution
- Meal history list with filtering options

### 8. Campus Gym Status

**Description**: Screen showing if campus gym is open/closed with hours information.

**Technical Requirements**:

- Create a `GymInfo` data class to store gym hours and status
- Load gym data from a local JSON asset file
- Implement time comparison logic to determine current open/closed status
- Design a `GymViewModel` to manage gym status logic

**UI Components**:

- Current status indicator (open/closed)
- Today's hours display
- Weekly schedule in expandable list or table format
- Basic gym information card

### 9. Basic Meditation Timer

**Description**: Countdown timer for meditation with session logging.

**Technical Requirements**:

- Create a `MeditationSession` data class to store session data
- Implement a custom `CountdownTimer` extending Android's `CountDownTimer`
- Design a `MeditationRepository` for data operations with GSON
- Create a `MeditationViewModel` to manage timer logic and session data

**UI Components**:

- Circular timer visualization with progress
- Duration selector with preset options
- Start/pause/reset controls
- Session completion animation/sound
- History log with total meditation minutes

### 10. Simple Health Dashboard

**Description**: Single screen showing today's basic health stats.

**Technical Requirements**:

- Create a `DashboardViewModel` to aggregate data from all repositories
- Implement data loading logic to display latest metrics
- Design utility methods to calculate daily stats and reset functionality

**UI Components**:

- Grid layout with summary cards for each health metric
- Quick action buttons for common tasks
- Visual indicators for goal progress
- Pull-to-refresh functionality

### 11. Basic Goal Tracking

**Description**: Interface to set health-related goals and track progress.

**Technical Requirements**:

- Create a `Goal` data class with fields for type, target, deadline, progress
- Implement a `GoalRepository` for data operations with GSON
- Design a `GoalViewModel` to manage goal tracking logic
- Create utility methods to calculate progress percentages

**UI Components**:

- Goal creation form with type, target, and deadline inputs
- Active goals list with progress bars
- Completed goals history
- Achievement badges or rewards system

### 12. Data Export

**Description**: Functionality to export health data as a JSON file.

**Technical Requirements**:

- Create an `ExportManager` class to handle file operations
- Implement JSON data aggregation from all repositories
- Utilize Android's FileProvider for sharing exported files
- Handle storage permissions for saving files

**Permission Requirements**:

- `android.permission.WRITE_EXTERNAL_STORAGE` for saving files
- Handle runtime permissions for Android 6+ devices

**UI Components**:

- Export options screen with date range selection
- File format options
- Export progress dialog
- Success notification with share option

## Performance Considerations

- Optimize sensor usage for battery efficiency
- Implement lazy loading for history views
- Use background threads for data operations
- Consider implementing WorkManager for scheduled tasks

## Security Considerations

- Encrypt sensitive user data
- Do not store personal health information in plain text
- Implement proper input validation to prevent injection attacks
- Consider adding PIN/biometric protection for app access

## Accessibility Requirements

- Support for screen readers
- High contrast mode
- Appropriate content descriptions for all UI elements
- Support for different text sizes

## Testing Requirements

- Unit tests for all repository and ViewModel classes
- UI tests for critical user flows
- Sensor simulation for step counter testing
- Device testing on various screen sizes and API levels

## Delivery Requirements

- Source code with proper documentation
- APK file
- User guide
- Technical documentation

# FitTracker Health App

An Android application for tracking various health metrics, including steps, water intake, mood, meals, workouts, meditation, and sleep.

## Project Structure

The application follows a clean architecture pattern with the following components:

### Core Components

- **Activities**: User interface screens (`DashboardActivity`, `HealthDashboardActivity`, etc.)
- **Models**: Data classes (`User`, `StepData`, `WaterIntake`, etc.)
- **ViewModels**: Manage UI-related data and business logic
- **Repositories**: Data access layer for persistence
- **Utils**: Helper classes and utilities

### Features

1. **User Management**
   - Login/Registration
   - Profile Management (height, weight, BMI)

2. **Health Tracking**
   - Step Counter
   - Water Intake Tracking
   - Mood Tracking
   - Meal Logging
   - Workout Tracking
   - Meditation Timer
   - Sleep Tracking

3. **Data Analysis**
   - Health Dashboard
   - Progress Tracking
   - Goal Setting

4. **Data Export**
   - JSON export of all health data
   - Save to device storage
   - Share functionality

## Data Export Feature

The health data export feature allows users to:

1. **Select Data Categories**:
   - User profile data
   - Activity data (steps, workouts)
   - Nutrition data (water, meals)
   - Wellbeing data (mood, sleep)

2. **Export Options**:
   - Save to device storage
   - Save and share immediately

3. **Output Format**:
   - JSON file with organized data structure
   - Timestamp and metadata

4. **Access Points**:
   - Main Dashboard menu
   - Health Dashboard menu

## File Organization

- **`app/src/main/java/com/example/cs4084_group_01/`**: Core application code
  - **`model/`**: Data classes
  - **`repository/`**: Data access layer
  - **`viewmodel/`**: ViewModels for UI state management
  - **`util/`**: Utility classes
  - **`*.java`**: Activity classes

- **`app/src/main/res/`**: Resources
  - **`layout/`**: UI layouts
  - **`drawable/`**: Graphics and icons
  - **`values/`**: Colors, strings, styles
  - **`menu/`**: Menu definitions
  - **`xml/`**: XML configurations

## Permissions

The application requires the following permissions:

- `ACTIVITY_RECOGNITION`: For step counting
- `FOREGROUND_SERVICE`: For background step counting service
- `POST_NOTIFICATIONS`: For notifications
- `WRITE_EXTERNAL_STORAGE`: For exporting data (Android < 10) 