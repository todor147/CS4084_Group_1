package com.example.cs4084_group_01.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to manage unit preferences across the application
 */
public class UnitManager {
    private static final String PREFS_NAME = "fit_tracker_prefs";
    private static final String METRIC_UNITS_PREF = "metric_units_enabled";
    
    // Unit conversion factors
    private static final float CM_TO_FEET = 0.0328084f;
    private static final float KG_TO_LBS = 2.20462f;
    private static final float ML_TO_FL_OZ = 0.033814f;
    
    /**
     * Checks if metric units are enabled
     * @param context Application context
     * @return true if metric units are enabled, false for imperial
     */
    public static boolean isMetricUnits(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(METRIC_UNITS_PREF, true);
    }
    
    /**
     * Sets whether metric units should be used
     * @param context Application context
     * @param useMetric true to use metric, false for imperial
     */
    public static void setMetricUnits(Context context, boolean useMetric) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(METRIC_UNITS_PREF, useMetric);
        editor.apply();
    }
    
    /**
     * Formats a height value based on current unit preferences
     * @param context Application context
     * @param heightCm Height in centimeters (metric)
     * @return Formatted height string in the preferred unit
     */
    public static String formatHeight(Context context, float heightCm) {
        if (isMetricUnits(context)) {
            return String.format("%.1f cm", heightCm);
        } else {
            float heightFt = heightCm * CM_TO_FEET;
            return String.format("%.1f ft", heightFt);
        }
    }
    
    /**
     * Formats a weight value based on current unit preferences
     * @param context Application context
     * @param weightKg Weight in kilograms (metric)
     * @return Formatted weight string in the preferred unit
     */
    public static String formatWeight(Context context, float weightKg) {
        if (isMetricUnits(context)) {
            return String.format("%.1f kg", weightKg);
        } else {
            float weightLbs = weightKg * KG_TO_LBS;
            return String.format("%.1f lbs", weightLbs);
        }
    }
    
    /**
     * Formats a volume value (water intake) based on current unit preferences
     * @param context Application context
     * @param volumeMl Volume in milliliters (metric)
     * @return Formatted volume string in the preferred unit
     */
    public static String formatVolume(Context context, int volumeMl) {
        if (isMetricUnits(context)) {
            if (volumeMl >= 1000) {
                float volumeL = volumeMl / 1000f;
                return String.format("%.1f L", volumeL);
            } else {
                return String.format("%d ml", volumeMl);
            }
        } else {
            float volumeFlOz = volumeMl * ML_TO_FL_OZ;
            return String.format("%.1f fl oz", volumeFlOz);
        }
    }
    
    /**
     * Converts a value from metric to the current unit system
     * @param context Application context
     * @param metricValue The value in metric units
     * @param conversionFactor The conversion factor to apply if imperial
     * @return The value in the current unit system
     */
    public static float convertToCurrentUnit(Context context, float metricValue, float conversionFactor) {
        if (isMetricUnits(context)) {
            return metricValue;
        } else {
            return metricValue * conversionFactor;
        }
    }
    
    /**
     * Converts a value from the current unit system to metric
     * @param context Application context
     * @param value The value in the current unit system
     * @param conversionFactor The conversion factor to apply if imperial
     * @return The value in metric units
     */
    public static float convertToMetric(Context context, float value, float conversionFactor) {
        if (isMetricUnits(context)) {
            return value;
        } else {
            return value / conversionFactor;
        }
    }
    
    /**
     * Gets the CM_TO_FEET conversion factor
     */
    public static float getCmToFeetFactor() {
        return CM_TO_FEET;
    }
    
    /**
     * Gets the KG_TO_LBS conversion factor
     */
    public static float getKgToLbsFactor() {
        return KG_TO_LBS;
    }
    
    /**
     * Gets the ML_TO_FL_OZ conversion factor
     */
    public static float getMlToFlOzFactor() {
        return ML_TO_FL_OZ;
    }
} 