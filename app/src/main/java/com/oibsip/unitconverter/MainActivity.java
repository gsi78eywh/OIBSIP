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
import com.oibsip.unitconverter.converter.UnitConverter;
import com.oibsip.unitconverter.model.Category;
import com.oibsip.unitconverter.model.Unit;

import java.util.List;

/**
 * MainActivity for the OIBSIP Unit Converter application.
 * 
 * Responsibilities:
 * 1. Initialize and bind UI widgets from XML layout.
 * 2. Populate Category and Unit dropdown spinners.
 * 3. Validate user input (empty text, non-numeric, below absolute zero).
 * 4. Execute conversion calculation via UnitConverter and display results.
 * 5. Provide utility actions: Swap units, Reset form, and Copy result.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UnitConverterApp";

    // Dropdown Spinners
    private Spinner spinnerCategory;
    private Spinner spinnerFromUnit;
    private Spinner spinnerToUnit;

    // Input Field
    private TextInputEditText etValueInput;

    // Action Buttons
    private MaterialButton btnConvert;
    private MaterialButton btnSwapUnits;
    private MaterialButton btnReset;
    private MaterialButton btnCopyResult;

    // Result Display Labels
    private TextView tvResultValue;
    private TextView tvResultUnit;
    private TextView tvResultFormula;

    // Stores formatted text for clipboard copying (e.g., "100 cm")
    private String lastResult = "";

    // =========================================================================
    // ACTIVITY LIFECYCLE
    // =========================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupCategorySpinner();
        setupButtons();
    }

    // =========================================================================
    // UI INITIALIZATION & SETUP
    // =========================================================================

    /**
     * Connects Java member variables to layout views declared in activity_main.xml.
     */
    private void initViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFromUnit = findViewById(R.id.spinnerFromUnit);
        spinnerToUnit   = findViewById(R.id.spinnerToUnit);
        etValueInput    = findViewById(R.id.etValueInput);

        btnConvert      = findViewById(R.id.btnConvert);
        btnSwapUnits    = findViewById(R.id.btnSwapUnits);
        btnReset        = findViewById(R.id.btnReset);
        btnCopyResult   = findViewById(R.id.btnCopyResult);

        tvResultValue   = findViewById(R.id.tvResultValue);
        tvResultUnit    = findViewById(R.id.tvResultUnit);
        tvResultFormula = findViewById(R.id.tvResultFormula);
    }

    /**
     * Fills the category dropdown with all available measurement categories
     * (Length, Weight, Temperature, Volume, Speed, Time).
     */
    private void setupCategorySpinner() {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                Category.values()
        );
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCategory.setAdapter(adapter);

        // When user picks a category, populate the source and target unit dropdowns
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                Log.d(TAG, "User selected category: " + selectedCategory.getDisplayName());
                updateUnitSpinners(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Populates both the "From Unit" and "To Unit" dropdowns based on the chosen category.
     */
    private void updateUnitSpinners(Category category) {
        List<Unit> units = UnitConverter.getUnitsForCategory(category);

        ArrayAdapter<Unit> unitAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                units
        );
        unitAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

        spinnerFromUnit.setAdapter(unitAdapter);
        spinnerToUnit.setAdapter(unitAdapter);

        // Set default selections (From = 1st unit, To = 2nd unit)
        if (units.size() > 1) {
            spinnerFromUnit.setSelection(0);
            spinnerToUnit.setSelection(1);
        }

        resetResult();
    }

    /**
     * Attaches click listeners to interactive action buttons.
     */
    private void setupButtons() {
        btnConvert.setOnClickListener(v -> performConversion());
        btnSwapUnits.setOnClickListener(v -> swapUnits());
        btnReset.setOnClickListener(v -> resetForm());
        btnCopyResult.setOnClickListener(v -> copyResultToClipboard());

        View btnOpenStopwatch = findViewById(R.id.btnOpenStopwatch);
        if (btnOpenStopwatch != null) {
            btnOpenStopwatch.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.oibsip.stopwatch.StopwatchActivity.class);
                startActivity(intent);
            });
        }
    }

    // =========================================================================
    // CORE USER ACTIONS
    // =========================================================================

    /**
     * Validates input, computes conversion using UnitConverter, and updates UI.
     */
    private void performConversion() {
        hideKeyboard();

        String rawInput = etValueInput.getText() != null ? etValueInput.getText().toString().trim() : "";
        Log.d(TAG, "Attempting conversion with input: '" + rawInput + "'");

        // 1. Validation: Empty field
        if (rawInput.isEmpty()) {
            Log.w(TAG, "Validation failed: Input field is empty");
            showToast("Please enter a numeric value to convert");
            return;
        }

        // 2. Validation: Valid number format
        double value;
        try {
            value = Double.parseDouble(rawInput);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Validation failed: Non-numeric value '" + rawInput + "'");
            showToast("Please enter a valid number");
            return;
        }

        Unit fromUnit = (Unit) spinnerFromUnit.getSelectedItem();
        Unit toUnit   = (Unit) spinnerToUnit.getSelectedItem();
        if (fromUnit == null || toUnit == null) {
            return;
        }

        // 3. Validation: Temperature absolute zero limit
        if (UnitConverter.isBelowAbsoluteZero(value, fromUnit)) {
            Log.w(TAG, "Validation failed: Temperature below Absolute Zero (" + value + " " + fromUnit.getSymbol() + ")");
            showToast("Invalid: Temperature cannot be below Absolute Zero");
            return;
        }

        // 4. Perform calculation
        Log.d(TAG, "Converting: " + value + " " + fromUnit.getName() + " -> " + toUnit.getName());
        double result = UnitConverter.convert(value, fromUnit, toUnit);
        String formattedResult = UnitConverter.formatResult(result);
        String formulaExplanation = UnitConverter.getFormulaExplanation(value, fromUnit, result, toUnit);

        Log.d(TAG, "Conversion successful: " + formattedResult + " " + toUnit.getSymbol());
        Log.d(TAG, "Formula: " + formulaExplanation);

        // 5. Update UI with results
        tvResultValue.setText(formattedResult);
        tvResultUnit.setText(toUnit.getSymbol());
        tvResultFormula.setText(formulaExplanation);
        lastResult = formattedResult + " " + toUnit.getSymbol();
    }

    /**
     * Swaps the selected source unit and target unit, and recalculates if value exists.
     */
    private void swapUnits() {
        int fromPos = spinnerFromUnit.getSelectedItemPosition();
        int toPos   = spinnerToUnit.getSelectedItemPosition();

        if (fromPos >= 0 && toPos >= 0) {
            spinnerFromUnit.setSelection(toPos);
            spinnerToUnit.setSelection(fromPos);
            Log.d(TAG, "Swapped unit positions: " + fromPos + " <-> " + toPos);
            showToast("Units swapped");

            // Recalculate immediately if an input value is already entered
            if (etValueInput.getText() != null && !etValueInput.getText().toString().trim().isEmpty()) {
                performConversion();
            }
        }
    }

    /**
     * Clears input field and resets results card to initial blank state.
     */
    private void resetForm() {
        etValueInput.setText("");
        resetResult();
        showToast("Form reset");
    }

    /**
     * Resets result card display text to placeholder state.
     */
    private void resetResult() {
        tvResultValue.setText("---");
        tvResultUnit.setText("");
        tvResultFormula.setText("Enter a number and tap Convert to see the result");
        lastResult = "";
    }

    /**
     * Copies the latest converted result to Android's system clipboard.
     */
    private void copyResultToClipboard() {
        if (lastResult.isEmpty()) {
            showToast("No result to copy");
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Conversion Result", lastResult));
            Log.d(TAG, "Copied to clipboard: " + lastResult);
            showToast("Copied to clipboard!");
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}
