# Health and Wellness Tracker - Technical Specification

## Overview

This document outlines the technical specifications for a Health and Wellness Tracker Android application written in Java. The application helps users monitor various aspects of their health including steps, water intake, sleep, mood, workouts, meditation, and meals through an intuitive interface with a centralized dashboard.

## Target Platform

- Android SDK 21+ (Android 5.0 Lollipop and above)
- Java (not Kotlin)
- Android Studio development environment

## Development Setup

- Android Gradle Plugin version 8.8.0
- Gradle 8.x compatible
- Android Studio Iguana or later recommended
- Material Components library for UI elements
- AndroidX components for modern Android development

## Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern for clean separation of concerns:

- **Model**: Data classes and repositories for storing and retrieving user health data
- **View**: Activities and Fragments for UI representation
- **ViewModel**: Classes to handle business logic and communication between View and Model

## Data Storage

- All data is stored locally using GSON for serialization/deserialization
- SharedPreferences is used for user settings and profile information
- Custom repository classes handle all data operations for different health metrics

## Dependencies

- AndroidX libraries for UI components
- GSON (com.google.code.gson:gson:2.10.1) for JSON serialization
- Material Design components for consistent UI
- Android Sensor APIs for step counting

## Features Specification

### 1. Basic User Profile

**Description**: Simple profile system to store essential user metrics and basic health goals.

**Technical Requirements**:

- `UserProfile` data class stores user information (age, height, weight, goals)
- GSON serialization/deserialization for profile data
- `ProfileRepository` handles profile data operations
- Material Design form components with appropriate input validation
- `ProfileViewModel` manages the data flow between UI and repository

**UI Components**:

- Profile creation/edit form with Material Design TextInputLayout
- Profile dashboard with user metrics displayed as cards
- Form validation with appropriate error messages

### 2. Simple Step Counter

**Description**: Basic step counter using device's built-in sensors with goal setting and history.

**Technical Requirements**:

- `StepCounterService` extends `Service` to run in the background
- Uses `SensorManager` and `Sensor.TYPE_STEP_DETECTOR` or `Sensor.TYPE_STEP_COUNTER`
- `StepData` model class stores daily steps
- `StepDataRepository` handles data persistence using GSON
- `StepViewModel` manages counter data and goals

**Permission Requirements**:

- `android.permission.ACTIVITY_RECOGNITION` for step counting
- Runtime permissions handling for Android 10+ devices

**UI Components**:

- Circular progress indicator showing steps vs goal
- History view with RecyclerView showing past day counts
- Goal setting dialog with number input

### 3. Water Intake Tracker

**Description**: Counter to log daily water consumption with goal setting functionality.

**Technical Requirements**:

- `WaterIntake` data class stores daily consumption data
- `WaterIntakeRepository` handles data operations with GSON
- `WaterViewModel` manages water intake tracking logic
- Utility methods calculate daily water requirements based on user profile

**UI Components**:

- Counter display with increment/decrement buttons
- Water bottle visualization that fills as consumption increases
- Daily history view with RecyclerView
- Water goal setting dialog

### 4. Sleep Logger

**Description**: Interface to manually log sleep duration and quality with history view.

**Technical Requirements**:

- `SleepEntry` data class stores sleep data (start time, end time, quality rating)
- `SleepRepository` handles data operations with GSON
- `SleepViewModel` manages sleep logging logic
- Time calculation utilities determine sleep duration
- `SleepStatistics` provides analysis of sleep patterns

**UI Components**:

- Time picker dialogs for sleep start and end times
- Rating bar (1-5) for sleep quality
- Weekly sleep summary with chart visualization
- History view with RecyclerView showing past sleep entries

### 5. Mood Tracker

**Description**: Daily mood check-in with basic emotion options and notes.

**Technical Requirements**:

- `MoodEntry` data class stores mood data (mood type, timestamp, notes)
- `MoodType` enum defines mood types (e.g., HAPPY, SAD, NEUTRAL, ANXIOUS, ENERGETIC)
- `MoodRepository` handles data operations with GSON
- `MoodViewModel` manages mood tracking logic
- Notification for daily mood check-in reminders

**UI Components**:

- Grid of mood icons for selection
- Optional notes input field
- Calendar view showing mood history with color coding
- Monthly mood summary statistics

### 6. Basic Workout Logger

**Description**: Form to log workout type, duration, and intensity with history.

**Technical Requirements**:

- `WorkoutEntry` data class stores workout data
- Defined types for workout types and intensity levels
- `WorkoutRepository` handles data operations with GSON
- Workout logging logic managed through dedicated activities

**UI Components**:

- Workout type selection spinner or radio buttons
- Duration input with number picker
- Intensity selection with slider or radio buttons
- History list with RecyclerView
- Summary statistics view

### 7. Simple Meal Tracker

**Description**: Basic meal logging by type with food group categorization.

**Technical Requirements**:

- `MealEntry` data class stores meal data
- `MealType` enum defines meal types (BREAKFAST, LUNCH, DINNER, SNACK)
- `FoodGroup` enum implements food group categorization system
- `MealRepository` handles data operations with GSON
- `MealViewModel` manages meal tracking logic

**UI Components**:

- Meal type selection tabs
- Food group selection checklist
- Optional meal description field
- Daily nutrition summary with food group distribution
- Meal history list with filtering options

### 8. Campus Gym Status

**Description**: Screen showing if campus gym is open/closed with hours information.

**Technical Requirements**:

- `GymInfo` data class stores gym hours and status
- Gym data loaded from a local JSON asset file
- Time comparison logic determines current open/closed status
- `GymInfoRepository` manages gym status data

**UI Components**:

- Current status indicator (open/closed)
- Today's hours display
- Weekly schedule in expandable list or table format
- Basic gym information card

### 9. Basic Meditation Timer

**Description**: Countdown timer for meditation with session logging.

**Technical Requirements**:

- `MeditationSession` data class stores session data
- Custom implementation extending Android's `CountDownTimer`
- `MeditationRepository` handles data operations with GSON
- Session management logic in dedicated activity

**UI Components**:

- Circular timer visualization with progress
- Duration selector with preset options
- Start/pause/reset controls
- Session completion animation/sound
- History log with total meditation minutes

### 10. Simple Health Dashboard

**Description**: Single screen showing today's basic health stats.

**Technical Requirements**:

- `HealthDashboardViewModel` aggregates data from all repositories
- Data loading logic displays latest metrics
- Utility methods calculate daily stats and reset functionality

**UI Components**:

- Grid layout with summary cards for each health metric
- Quick action buttons for common tasks 
- Visual indicators for goal progress
- Navigation to detailed tracking activities

### 11. Basic Goal Tracking

**Description**: Interface to set health-related goals and track progress.

**Technical Requirements**:

- `Goal` data class with fields for type, target, deadline, progress
- `GoalRepository` handles data operations with GSON
- `GoalViewModel` manages goal tracking logic
- Utility methods calculate progress percentages

**UI Components**:

- Goal creation form with type, target, and deadline inputs
- Active goals list with progress bars
- Completed goals history
- Achievement badges or rewards system

### 12. Data Export

**Description**: Functionality to export health data as a JSON file.

**Technical Requirements**:

- `ExportDataActivity` handles file operations
- JSON data aggregation from all repositories
- Android's FileProvider for sharing exported files
- Storage permissions handling for saving files

**Permission Requirements**:

- `android.permission.WRITE_EXTERNAL_STORAGE` for saving files
- Runtime permissions handling for Android 6+ devices

**UI Components**:

- Export options screen with date range selection
- File format options
- Export progress dialog
- Success notification with share option

## Performance Considerations

- Optimized sensor usage for battery efficiency
- Lazy loading for history views
- Background threads for data operations
- Efficient data storage and retrieval patterns

## Security Considerations

- User data is stored locally on device
- Input validation prevents injection attacks
- No personal health information is transmitted over networks

## Accessibility Requirements

- Support for screen readers
- Appropriate content descriptions for UI elements
- Support for different text sizes

## Testing Requirements

- Unit tests for repository and ViewModel classes
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

The application follows the MVVM architecture pattern with the following components:

### Core Components

- **Activities**: User interface screens (`DashboardActivity`, `HealthDashboardActivity`, etc.)
- **Models**: Data classes (`UserProfile`, `StepData`, `WaterIntake`, etc.)
- **ViewModels**: Manage UI-related data and business logic
- **Repositories**: Data access layer for persistence
- **Utils**: Helper classes and utilities

### Features

1. **User Management**
   - Login/Registration
   - Profile Management (height, weight, BMI)

2. **Health Tracking**
   - Step Counter with background service
   - Water Intake Tracking with visualization
   - Mood Tracking with emotion selection
   - Meal Logging with food group categorization
   - Workout Tracking with types and intensity
   - Meditation Timer with session tracking
   - Sleep Tracking with quality assessment

3. **Data Analysis**
   - Health Dashboard with all metrics
   - Progress Tracking with visual indicators
   - Goal Setting and completion tracking

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
   - Settings menu

## File Organization

- **`app/src/main/java/com/example/cs4084_group_01/`**: Core application code
  - **`model/`**: Data classes for health metrics
  - **`repository/`**: Data access layer using GSON
  - **`viewmodel/`**: ViewModels for UI state management
  - **`util/`**: Utility classes and helpers
  - **`adapter/`**: RecyclerView adapters
  - **`fragment/`**: UI fragments
  - **`service/`**: Background services
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