package com.oibsip.unitconverter.converter;

import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UnitConverter {

    private static final Map<Category, List<Unit>> UNITS_MAP = new EnumMap<>(Category.class);

    static {
        registerLengthUnits();
        registerWeightUnits();
        registerTemperatureUnits();
        registerVolumeUnits();
        registerSpeedUnits();
        registerTimeUnits();
    }

    private static void registerLengthUnits() {

        List<Unit> units = new ArrayList<>();
        units.add(new Unit("len_mm", "Millimeter", "mm", Category.LENGTH, 0.001));
        units.add(new Unit("len_cm", "Centimeter", "cm", Category.LENGTH, 0.01));
        units.add(new Unit("len_m",  "Meter",      "m",  Category.LENGTH, 1.0));
        units.add(new Unit("len_km", "Kilometer",  "km", Category.LENGTH, 1000.0));
        units.add(new Unit("len_in", "Inch",       "in", Category.LENGTH, 0.0254));
        units.add(new Unit("len_ft", "Foot",       "ft", Category.LENGTH, 0.3048));
        units.add(new Unit("len_yd", "Yard",       "yd", Category.LENGTH, 0.9144));
        units.add(new Unit("len_mi", "Mile",       "mi", Category.LENGTH, 1609.344));
        UNITS_MAP.put(Category.LENGTH, Collections.unmodifiableList(units));
    }

    private static void registerWeightUnits() {

        List<Unit> units = new ArrayList<>();
        units.add(new Unit("wt_mg",  "Milligram",  "mg", Category.WEIGHT, 0.000001));
        units.add(new Unit("wt_g",   "Gram",       "g",  Category.WEIGHT, 0.001));
        units.add(new Unit("wt_kg",  "Kilogram",   "kg", Category.WEIGHT, 1.0));
        units.add(new Unit("wt_ton", "Metric Ton", "t",  Category.WEIGHT, 1000.0));
        units.add(new Unit("wt_oz",  "Ounce",      "oz", Category.WEIGHT, 0.028349523125));
        units.add(new Unit("wt_lb",  "Pound",      "lb", Category.WEIGHT, 0.45359237));
        UNITS_MAP.put(Category.WEIGHT, Collections.unmodifiableList(units));
    }

    private static void registerTemperatureUnits() {

        List<Unit> units = new ArrayList<>();
        units.add(new Unit("temp_c", "Celsius",    "°C", Category.TEMPERATURE, 1.0));
        units.add(new Unit("temp_f", "Fahrenheit", "°F", Category.TEMPERATURE, 1.0));
        units.add(new Unit("temp_k", "Kelvin",     "K",  Category.TEMPERATURE, 1.0));
        UNITS_MAP.put(Category.TEMPERATURE, Collections.unmodifiableList(units));

    }

    private static void registerVolumeUnits() {

        List<Unit> units = new ArrayList<>();
        units.add(new Unit("vol_ml",   "Milliliter",     "mL",    Category.VOLUME, 0.001));
        units.add(new Unit("vol_l",    "Liter",          "L",     Category.VOLUME, 1.0));
        units.add(new Unit("vol_floz", "US Fluid Ounce", "fl oz", Category.VOLUME, 0.0295735295625));
        units.add(new Unit("vol_cup",  "US Cup",         "cup",   Category.VOLUME, 0.2365882365));
        units.add(new Unit("vol_pt",   "US Pint",        "pt",    Category.VOLUME, 0.473176473));
        units.add(new Unit("vol_gal",  "US Gallon",      "gal",   Category.VOLUME, 3.785411784));
        UNITS_MAP.put(Category.VOLUME, Collections.unmodifiableList(units));
    }

    private static void registerSpeedUnits() {

        List<Unit> units = new ArrayList<>();
        units.add(new Unit("spd_ms",   "Meter per second",   "m/s",  Category.SPEED, 1.0));
        units.add(new Unit("spd_kmh",  "Kilometer per hour", "km/h", Category.SPEED, 1.0 / 3.6));
        units.add(new Unit("spd_mph",  "Mile per hour",      "mph",  Category.SPEED, 0.44704));
        units.add(new Unit("spd_knot", "Knot",               "kn",   Category.SPEED, 0.5144444444444445));
        UNITS_MAP.put(Category.SPEED, Collections.unmodifiableList(units));
    }

    private static void registerTimeUnits() {

        List<Unit> units = new ArrayList<>();
        units.add(new Unit("time_ms",  "Millisecond", "ms",  Category.TIME, 0.001));
        units.add(new Unit("time_s",   "Second",      "s",   Category.TIME, 1.0));
        units.add(new Unit("time_min", "Minute",      "min", Category.TIME, 60.0));
        units.add(new Unit("time_h",   "Hour",        "hr",  Category.TIME, 3600.0));
        units.add(new Unit("time_d",   "Day",         "d",   Category.TIME, 86400.0));
        UNITS_MAP.put(Category.TIME, Collections.unmodifiableList(units));
    }

    public static List<Unit> getUnitsForCategory(Category category) {
        if (category == null) {
            return Collections.emptyList();
        }
        return UNITS_MAP.getOrDefault(category, Collections.emptyList());
    }

    public static boolean isBelowAbsoluteZero(double value, Unit unit) {
        if (unit.getCategory() != Category.TEMPERATURE) {
            return false;
        }

        switch (unit.getId()) {
            case "temp_c": return value < -273.15;
            case "temp_k": return value < 0.0;
            case "temp_f": return value < -459.67;
            default:       return false;
        }
    }

    public static double convert(double value, Unit from, Unit to) {

        if (from.equals(to)) {
            return value;
        }

        if (from.getCategory() == Category.TEMPERATURE) {
            return convertTemperature(value, from.getId(), to.getId());
        }

        double valueInBaseUnit = value * from.getFactorToBase();
        return valueInBaseUnit / to.getFactorToBase();
    }

    private static double convertTemperature(double value, String fromId, String toId) {

        double celsius = value;
        if ("temp_f".equals(fromId)) {
            celsius = (value - 32.0) * 5.0 / 9.0;
        } else if ("temp_k".equals(fromId)) {
            celsius = value - 273.15;
        }

        if ("temp_f".equals(toId)) {
            return (celsius * 9.0 / 5.0) + 32.0;
        } else if ("temp_k".equals(toId)) {
            return celsius + 273.15;
        }
        return celsius;
    }

    public static String formatResult(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }
        DecimalFormat df = new DecimalFormat("#,##0.######", new DecimalFormatSymbols(Locale.US));
        return df.format(value);
    }

    public static String getFormulaExplanation(double input, Unit from, double result, Unit to) {

        if (from.equals(to)) {
            return "1 " + from.getSymbol() + " = 1 " + to.getSymbol();
        }

        if (from.getCategory() == Category.TEMPERATURE) {
            if ("temp_c".equals(from.getId()) && "temp_f".equals(to.getId())) return "Formula: (°C × 9/5) + 32 = °F";
            if ("temp_f".equals(from.getId()) && "temp_c".equals(to.getId())) return "Formula: (°F − 32) × 5/9 = °C";
            if ("temp_c".equals(from.getId()) && "temp_k".equals(to.getId())) return "Formula: °C + 273.15 = K";
            if ("temp_k".equals(from.getId()) && "temp_c".equals(to.getId())) return "Formula: K − 273.15 = °C";
            if ("temp_f".equals(from.getId()) && "temp_k".equals(to.getId())) return "Formula: (°F − 32) × 5/9 + 273.15 = K";
            if ("temp_k".equals(from.getId()) && "temp_f".equals(to.getId())) return "Formula: (K − 273.15) × 9/5 + 32 = °F";
        }

        double ratio = from.getFactorToBase() / to.getFactorToBase();
        return "1 " + from.getSymbol() + " = " + formatResult(ratio) + " " + to.getSymbol();
    }
}
