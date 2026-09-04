package com.oibsip.unitconverter.converter;

import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pure Java conversion engine supporting linear and non-linear unit conversions,
 * input validation, formatting, and formula generation.
 */
public class UnitConverter {

    private static final Map<Category, List<Unit>> UNITS_BY_CATEGORY = new EnumMap<>(Category.class);

    static {
        // --- Length (Base: Meter) ---
        List<Unit> lengthUnits = new ArrayList<>();
        lengthUnits.add(new Unit("len_mm", "Millimeter", "mm", Category.LENGTH, 0.001));
        lengthUnits.add(new Unit("len_cm", "Centimeter", "cm", Category.LENGTH, 0.01));
        lengthUnits.add(new Unit("len_m", "Meter", "m", Category.LENGTH, 1.0));
        lengthUnits.add(new Unit("len_km", "Kilometer", "km", Category.LENGTH, 1000.0));
        lengthUnits.add(new Unit("len_in", "Inch", "in", Category.LENGTH, 0.0254));
        lengthUnits.add(new Unit("len_ft", "Foot", "ft", Category.LENGTH, 0.3048));
        lengthUnits.add(new Unit("len_yd", "Yard", "yd", Category.LENGTH, 0.9144));
        lengthUnits.add(new Unit("len_mi", "Mile", "mi", Category.LENGTH, 1609.344));
        UNITS_BY_CATEGORY.put(Category.LENGTH, Collections.unmodifiableList(lengthUnits));

        // --- Weight / Mass (Base: Kilogram) ---
        List<Unit> weightUnits = new ArrayList<>();
        weightUnits.add(new Unit("wt_mg", "Milligram", "mg", Category.WEIGHT, 0.000001));
        weightUnits.add(new Unit("wt_g", "Gram", "g", Category.WEIGHT, 0.001));
        weightUnits.add(new Unit("wt_kg", "Kilogram", "kg", Category.WEIGHT, 1.0));
        weightUnits.add(new Unit("wt_ton", "Metric Ton", "t", Category.WEIGHT, 1000.0));
        weightUnits.add(new Unit("wt_oz", "Ounce", "oz", Category.WEIGHT, 0.028349523125));
        weightUnits.add(new Unit("wt_lb", "Pound", "lb", Category.WEIGHT, 0.45359237));
        UNITS_BY_CATEGORY.put(Category.WEIGHT, Collections.unmodifiableList(weightUnits));

        // --- Temperature (Non-linear) ---
        List<Unit> tempUnits = new ArrayList<>();
        tempUnits.add(new Unit("temp_c", "Celsius", "°C", Category.TEMPERATURE, 1.0));
        tempUnits.add(new Unit("temp_f", "Fahrenheit", "°F", Category.TEMPERATURE, 1.0));
        tempUnits.add(new Unit("temp_k", "Kelvin", "K", Category.TEMPERATURE, 1.0));
        UNITS_BY_CATEGORY.put(Category.TEMPERATURE, Collections.unmodifiableList(tempUnits));

        // --- Volume / Capacity (Base: Liter) ---
        List<Unit> volumeUnits = new ArrayList<>();
        volumeUnits.add(new Unit("vol_ml", "Milliliter", "mL", Category.VOLUME, 0.001));
        volumeUnits.add(new Unit("vol_l", "Liter", "L", Category.VOLUME, 1.0));
        volumeUnits.add(new Unit("vol_floz", "US Fluid Ounce", "fl oz", Category.VOLUME, 0.0295735295625));
        volumeUnits.add(new Unit("vol_cup", "US Cup", "cup", Category.VOLUME, 0.2365882365));
        volumeUnits.add(new Unit("vol_pt", "US Pint", "pt", Category.VOLUME, 0.473176473));
        volumeUnits.add(new Unit("vol_gal", "US Gallon", "gal", Category.VOLUME, 3.785411784));
        UNITS_BY_CATEGORY.put(Category.VOLUME, Collections.unmodifiableList(volumeUnits));

        // --- Speed / Velocity (Base: Meter per second) ---
        List<Unit> speedUnits = new ArrayList<>();
        speedUnits.add(new Unit("spd_ms", "Meter per second", "m/s", Category.SPEED, 1.0));
        speedUnits.add(new Unit("spd_kmh", "Kilometer per hour", "km/h", Category.SPEED, 1.0 / 3.6));
        speedUnits.add(new Unit("spd_mph", "Mile per hour", "mph", Category.SPEED, 0.44704));
        speedUnits.add(new Unit("spd_knot", "Knot", "kn", Category.SPEED, 0.5144444444444445));
        UNITS_BY_CATEGORY.put(Category.SPEED, Collections.unmodifiableList(speedUnits));

        // --- Time (Base: Second) ---
        List<Unit> timeUnits = new ArrayList<>();
        timeUnits.add(new Unit("time_ms", "Millisecond", "ms", Category.TIME, 0.001));
        timeUnits.add(new Unit("time_s", "Second", "s", Category.TIME, 1.0));
        timeUnits.add(new Unit("time_min", "Minute", "min", Category.TIME, 60.0));
        timeUnits.add(new Unit("time_h", "Hour", "hr", Category.TIME, 3600.0));
        timeUnits.add(new Unit("time_d", "Day", "d", Category.TIME, 86400.0));
        UNITS_BY_CATEGORY.put(Category.TIME, Collections.unmodifiableList(timeUnits));
    }

    /**
     * Retrieve all units belonging to a specific category.
     */
    public static List<Unit> getUnitsForCategory(Category category) {
        List<Unit> units = UNITS_BY_CATEGORY.get(category);
        return units != null ? units : Collections.emptyList();
    }

    /**
     * Check if a temperature value is below absolute zero (-273.15°C, 0 K, -459.67°F).
     */
    public static boolean isBelowAbsoluteZero(double value, Unit unit) {
        if (unit.getCategory() != Category.TEMPERATURE) {
            return false;
        }
        if ("temp_c".equals(unit.getId())) {
            return value < -273.15;
        } else if ("temp_k".equals(unit.getId())) {
            return value < 0.0;
        } else if ("temp_f".equals(unit.getId())) {
            return value < -459.67;
        }
        return false;
    }

    /**
     * Converts a numeric value from one unit to another.
     */
    public static double convert(double inputValue, Unit fromUnit, Unit toUnit) {
        if (fromUnit.equals(toUnit)) {
            return inputValue;
        }

        if (fromUnit.getCategory() == Category.TEMPERATURE) {
            return convertTemperature(inputValue, fromUnit, toUnit);
        }

        // Linear conversion: value -> base -> target
        double baseValue = inputValue * fromUnit.getFactorToBase();
        return baseValue / toUnit.getFactorToBase();
    }

    private static double convertTemperature(double value, Unit fromUnit, Unit toUnit) {
        // Step 1: convert to Celsius as intermediate
        double celsius;
        switch (fromUnit.getId()) {
            case "temp_f":
                celsius = (value - 32.0) * 5.0 / 9.0;
                break;
            case "temp_k":
                celsius = value - 273.15;
                break;
            case "temp_c":
            default:
                celsius = value;
                break;
        }

        // Step 2: convert Celsius to target unit
        switch (toUnit.getId()) {
            case "temp_f":
                return (celsius * 9.0 / 5.0) + 32.0;
            case "temp_k":
                return celsius + 273.15;
            case "temp_c":
            default:
                return celsius;
        }
    }

    /**
     * Formats a double value with high readability:
     * - Removes trailing zeros
     * - Uses up to 6 decimal places
     * - Uses scientific notation for extremely large or small numbers
     */
    public static String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return String.valueOf(value);
        }

        double abs = Math.abs(value);
        if (abs != 0 && (abs >= 1e11 || abs < 1e-5)) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            DecimalFormat sciFormat = new DecimalFormat("0.######E0", symbols);
            return sciFormat.format(value);
        }

        // Standard decimal formatting
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.######", symbols);
        return df.format(value);
    }

    /**
     * Generates a descriptive mathematical formula or conversion breakdown.
     */
    public static String getFormulaExplanation(double inputValue, Unit fromUnit, double resultValue, Unit toUnit) {
        if (fromUnit.equals(toUnit)) {
            return "Identical units (1 : 1 ratio)";
        }

        String formattedInput = formatResult(inputValue);
        String formattedResult = formatResult(resultValue);

        if (fromUnit.getCategory() == Category.TEMPERATURE) {
            String fromId = fromUnit.getId();
            String toId = toUnit.getId();

            if ("temp_c".equals(fromId) && "temp_f".equals(toId)) {
                return String.format(Locale.US, "Formula: (%s°C × 9/5) + 32 = %s°F", formattedInput, formattedResult);
            } else if ("temp_f".equals(fromId) && "temp_c".equals(toId)) {
                return String.format(Locale.US, "Formula: (%s°F − 32) × 5/9 = %s°C", formattedInput, formattedResult);
            } else if ("temp_c".equals(fromId) && "temp_k".equals(toId)) {
                return String.format(Locale.US, "Formula: %s°C + 273.15 = %s K", formattedInput, formattedResult);
            } else if ("temp_k".equals(fromId) && "temp_c".equals(toId)) {
                return String.format(Locale.US, "Formula: %s K − 273.15 = %s°C", formattedInput, formattedResult);
            } else if ("temp_f".equals(fromId) && "temp_k".equals(toId)) {
                return String.format(Locale.US, "Formula: (%s°F − 32) × 5/9 + 273.15 = %s K", formattedInput, formattedResult);
            } else if ("temp_k".equals(fromId) && "temp_f".equals(toId)) {
                return String.format(Locale.US, "Formula: (%s K − 273.15) × 9/5 + 32 = %s°F", formattedInput, formattedResult);
            }
        }

        // Linear ratio
        double ratio = fromUnit.getFactorToBase() / toUnit.getFactorToBase();
        String formattedRatio = formatResult(ratio);
        return String.format(Locale.US, "1 %s = %s %s  •  (%s × %s = %s)",
                fromUnit.getSymbol(), formattedRatio, toUnit.getSymbol(),
                formattedInput, formattedRatio, formattedResult);
    }
}
