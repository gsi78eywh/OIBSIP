package com.oibsip.unitconverter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.util.List;

/**
 * MainActivity: The primary screen and event controller of the Unit Converter application.
 *
 * DEBUGGING TIP:
 * Open the "Logcat" tab at the bottom of Android Studio and filter by "UnitConverterApp"
 * to see real-time debug logs for every button click, validation, and conversion calculation!
 */
public class MainActivity extends AppCompatActivity {

    // Log tag for easy filtering in Android Studio Logcat
    private static final String TAG = "UnitConverterApp";

    // --- UI View References ---
    private Spinner spinnerCategory;        // Dropdown to pick measurement category
    private Spinner spinnerFromUnit;        // Dropdown to pick source unit
    private Spinner spinnerToUnit;          // Dropdown to pick target unit
    private TextInputLayout tilValueInput;  // Input layout with error display
    private TextInputEditText etValueInput; // Text field where user types number
    private MaterialButton btnSwapUnits;    // Button to swap source and target units
    private MaterialButton btnConvert;      // Button to compute conversion
    private MaterialButton btnReset;        // Button to clear inputs and reset view
    private MaterialButton btnCopyResult;   // Button to copy result to clipboard
    private TextView tvResultValue;         // TextView showing converted number
    private TextView tvResultUnit;          // TextView showing converted unit symbol
    private TextView tvResultFormula;       // TextView showing mathematical formula

    // List of units currently available for the selected category
    private List<Unit> currentUnits;

    // Cache of the most recent converted result for the clipboard copy feature
    private String lastCalculatedResultText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity created successfully");

        // Step 1: Link Java variables to XML components
        initViews();

        // Step 2: Populate Category Spinner (Length, Weight, etc.)
        setupCategorySpinner();

        // Step 3: Register Click and Selection Listeners
        setupListeners();
    }

    /**
     * Binds XML layout view IDs to Java fields.
     */
    private void initViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFromUnit = findViewById(R.id.spinnerFromUnit);
        spinnerToUnit = findViewById(R.id.spinnerToUnit);
        tilValueInput = findViewById(R.id.tilValueInput);
        etValueInput = findViewById(R.id.etValueInput);
        btnSwapUnits = findViewById(R.id.btnSwapUnits);
        btnConvert = findViewById(R.id.btnConvert);
        btnReset = findViewById(R.id.btnReset);
        btnCopyResult = findViewById(R.id.btnCopyResult);
        tvResultValue = findViewById(R.id.tvResultValue);
        tvResultUnit = findViewById(R.id.tvResultUnit);
        tvResultFormula = findViewById(R.id.tvResultFormula);
        Log.d(TAG, "Views initialized and bound");
    }

    /**
     * Populates the Category Spinner with all available categories
     * and sets up the listener that updates the unit spinners when selected.
     */
    private void setupCategorySpinner() {
        Category[] categories = Category.values();
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                categories
        );
        categoryAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                Log.d(TAG, "User selected category: " + selectedCategory.getDisplayName());
                onCategoryChanged(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    /**
     * Called whenever a new category is selected.
     * Repopulates the From and To spinners with units belonging to that category.
     */
    private void onCategoryChanged(Category category) {
        currentUnits = UnitConverter.getUnitsForCategory(category);
        Log.d(TAG, "Loaded " + currentUnits.size() + " units for category: " + category.getDisplayName());

        ArrayAdapter<Unit> unitAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                currentUnits
        );
        unitAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

        spinnerFromUnit.setAdapter(unitAdapter);
        spinnerToUnit.setAdapter(unitAdapter);

        // Smart defaults: Set From = first unit, To = second unit (e.g. Centimeter -> Meter)
        if (currentUnits.size() > 1) {
            spinnerFromUnit.setSelection(0);
            spinnerToUnit.setSelection(1);
        } else if (!currentUnits.isEmpty()) {
            spinnerFromUnit.setSelection(0);
            spinnerToUnit.setSelection(0);
        }

        // Reset previous result since category changed
        resetResultView();
    }

    /**
     * Configures click listeners for all interactive buttons.
     */
    private void setupListeners() {
        // --- 1. Convert Button ---
        btnConvert.setOnClickListener(v -> {
            Log.d(TAG, "Convert button clicked");
            performConversion();
        });

        // --- 2. Swap Units Button ---
        btnSwapUnits.setOnClickListener(v -> {
            int fromPos = spinnerFromUnit.getSelectedItemPosition();
            int toPos = spinnerToUnit.getSelectedItemPosition();

            if (fromPos != AdapterView.INVALID_POSITION && toPos != AdapterView.INVALID_POSITION) {
                spinnerFromUnit.setSelection(toPos);
                spinnerToUnit.setSelection(fromPos);
                Log.d(TAG, "Swapped unit positions: " + fromPos + " <-> " + toPos);
                Toast.makeText(this, R.string.toast_units_swapped, Toast.LENGTH_SHORT).show();

                // If user has already entered a number, recalculate immediately
                if (etValueInput.getText() != null && !etValueInput.getText().toString().trim().isEmpty()) {
                    performConversion();
                }
            }
        });

        // --- 3. Reset Button ---
        btnReset.setOnClickListener(v -> {
            Log.d(TAG, "Reset button clicked");
            resetAll();
        });

        // --- 4. Copy Result Button ---
        btnCopyResult.setOnClickListener(v -> {
            Log.d(TAG, "Copy result button clicked");
            copyResultToClipboard();
        });
    }

    /**
     * Core Conversion Method:
     * Validates input, checks boundaries, executes math, and updates the UI.
     */
    private void performConversion() {
        // Dismiss soft keyboard so the user can easily see the result card
        dismissKeyboard();

        String rawInput = etValueInput.getText() != null ? etValueInput.getText().toString().trim() : "";
        Log.d(TAG, "Attempting conversion with input: '" + rawInput + "'");

        // --- VALIDATION CHECK 1: Is input empty? ---
        if (rawInput.isEmpty()) {
            Log.w(TAG, "Validation failed: Input field is empty");
            tilValueInput.setError(getString(R.string.toast_empty_input));
            Toast.makeText(this, R.string.toast_empty_input, Toast.LENGTH_SHORT).show();
            return;
        }

        tilValueInput.setError(null);

        // --- VALIDATION CHECK 2: Is input a valid number? ---
        double inputValue;
        try {
            inputValue = Double.parseDouble(rawInput);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Validation failed: Non-numeric value '" + rawInput + "'");
            tilValueInput.setError(getString(R.string.toast_invalid_number));
            Toast.makeText(this, R.string.toast_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected units from spinners
        Unit fromUnit = (Unit) spinnerFromUnit.getSelectedItem();
        Unit toUnit = (Unit) spinnerToUnit.getSelectedItem();

        if (fromUnit == null || toUnit == null) {
            Log.e(TAG, "Error: Selected units are null");
            return;
        }

        Log.d(TAG, "Converting: " + inputValue + " " + fromUnit.getName() + " -> " + toUnit.getName());

        // --- VALIDATION CHECK 3: Physical constraints (e.g. Absolute Zero) ---
        if (UnitConverter.isBelowAbsoluteZero(inputValue, fromUnit)) {
            Log.w(TAG, "Validation failed: Temperature below absolute zero (" + inputValue + " " + fromUnit.getSymbol() + ")");
            Toast.makeText(this, R.string.toast_temp_below_abs_zero, Toast.LENGTH_LONG).show();
            tilValueInput.setError(getString(R.string.toast_temp_below_abs_zero));
            return;
        }

        // --- EXECUTE CONVERSION ---
        double convertedValue = UnitConverter.convert(inputValue, fromUnit, toUnit);
        String formattedResult = UnitConverter.formatResult(convertedValue);
        String formulaExplanation = UnitConverter.getFormulaExplanation(inputValue, fromUnit, convertedValue, toUnit);

        Log.d(TAG, "Conversion successful: " + formattedResult + " " + toUnit.getSymbol());
        Log.d(TAG, "Formula: " + formulaExplanation);

        // --- UPDATE UI WITH RESULTS ---
        tvResultValue.setText(formattedResult);
        tvResultUnit.setText(toUnit.getSymbol());
        tvResultFormula.setText(formulaExplanation);

        lastCalculatedResultText = formattedResult + " " + toUnit.getSymbol();
    }

    /**
     * Resets input fields, error indicators, and calculation results.
     */
    private void resetAll() {
        etValueInput.setText("");
        tilValueInput.setError(null);
        resetResultView();
        dismissKeyboard();
        Log.d(TAG, "All inputs and results reset");
    }

    /**
     * Resets the result card to its default placeholder state.
     */
    private void resetResultView() {
        tvResultValue.setText(R.string.result_initial_placeholder);
        tvResultUnit.setText("");
        tvResultFormula.setText(R.string.result_formula_placeholder);
        lastCalculatedResultText = "";
    }

    /**
     * Copies the calculated result to the Android clipboard.
     */
    private void copyResultToClipboard() {
        if (lastCalculatedResultText.isEmpty()) {
            Log.w(TAG, "Copy failed: No result has been calculated yet");
            Toast.makeText(this, R.string.result_formula_placeholder, Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Conversion Result", lastCalculatedResultText);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Log.d(TAG, "Copied to clipboard: " + lastCalculatedResultText);
            Toast.makeText(this, R.string.toast_result_copied, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper to dismiss the software keyboard.
     */
    private void dismissKeyboard() {
        View currentFocusView = getCurrentFocus();
        if (currentFocusView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
            }
        }
    }
}
