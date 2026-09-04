package com.oibsip.unitconverter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory, spinnerFromUnit, spinnerToUnit;
    private TextInputEditText etValueInput;
    private MaterialButton btnConvert, btnSwapUnits, btnReset, btnCopyResult;
    private TextView tvResultValue, tvResultUnit, tvResultFormula;
    private String lastResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupCategorySpinner();
        setupButtons();
    }

    private void initViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFromUnit = findViewById(R.id.spinnerFromUnit);
        spinnerToUnit = findViewById(R.id.spinnerToUnit);
        etValueInput = findViewById(R.id.etValueInput);
        btnConvert = findViewById(R.id.btnConvert);
        btnSwapUnits = findViewById(R.id.btnSwapUnits);
        btnReset = findViewById(R.id.btnReset);
        btnCopyResult = findViewById(R.id.btnCopyResult);
        tvResultValue = findViewById(R.id.tvResultValue);
        tvResultUnit = findViewById(R.id.tvResultUnit);
        tvResultFormula = findViewById(R.id.tvResultFormula);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, Category.values());
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners((Category) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateUnitSpinners(Category category) {
        List<Unit> units = UnitConverter.getUnitsForCategory(category);
        ArrayAdapter<Unit> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, units);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);

        spinnerFromUnit.setAdapter(adapter);
        spinnerToUnit.setAdapter(adapter);

        if (units.size() > 1) {
            spinnerFromUnit.setSelection(0);
            spinnerToUnit.setSelection(1);
        }
        resetResult();
    }

    private void setupButtons() {
        btnConvert.setOnClickListener(v -> convert());
        btnSwapUnits.setOnClickListener(v -> swap());
        btnReset.setOnClickListener(v -> reset());
        btnCopyResult.setOnClickListener(v -> copy());
    }

    private void convert() {
        String text = etValueInput.getText() != null ? etValueInput.getText().toString().trim() : "";

        // Check if empty
        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter a numeric value to convert", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if numeric
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        Unit fromUnit = (Unit) spinnerFromUnit.getSelectedItem();
        Unit toUnit = (Unit) spinnerToUnit.getSelectedItem();
        if (fromUnit == null || toUnit == null) return;

        // Check temperature limits
        if (UnitConverter.isBelowAbsoluteZero(value, fromUnit)) {
            Toast.makeText(this, "Invalid: Temperature cannot be below Absolute Zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate and display
        double result = UnitConverter.convert(value, fromUnit, toUnit);
        String formattedResult = UnitConverter.formatResult(result);

        tvResultValue.setText(formattedResult);
        tvResultUnit.setText(toUnit.getSymbol());
        tvResultFormula.setText(UnitConverter.getFormulaExplanation(value, fromUnit, result, toUnit));
        lastResult = formattedResult + " " + toUnit.getSymbol();
    }

    private void swap() {
        int from = spinnerFromUnit.getSelectedItemPosition();
        int to = spinnerToUnit.getSelectedItemPosition();

        if (from >= 0 && to >= 0) {
            spinnerFromUnit.setSelection(to);
            spinnerToUnit.setSelection(from);
            Toast.makeText(this, "Units swapped", Toast.LENGTH_SHORT).show();

            if (etValueInput.getText() != null && !etValueInput.getText().toString().trim().isEmpty()) {
                convert();
            }
        }
    }

    private void reset() {
        etValueInput.setText("");
        resetResult();
    }

    private void resetResult() {
        tvResultValue.setText("---");
        tvResultUnit.setText("");
        tvResultFormula.setText("Enter a number and tap Convert to see the result");
        lastResult = "";
    }

    private void copy() {
        if (lastResult.isEmpty()) {
            Toast.makeText(this, "No result to copy", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Result", lastResult));
            Toast.makeText(this, "Converted result copied to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }
}
