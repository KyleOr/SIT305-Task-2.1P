package com.example.unitconverterapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;



import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // UI components
    Spinner spinnerCategory, spinnerSourceUnit, spinnerDestinationUnit;
    EditText editTextValue;
    Button convertButton;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSourceUnit = findViewById(R.id.spinnerSourceUnit);
        spinnerDestinationUnit = findViewById(R.id.spinnerDestinationUnit);
        editTextValue = findViewById(R.id.editTextValue);
        convertButton = findViewById(R.id.convertButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Set up category spinner (Length, Weight, Temperature)
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.unit_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set up the spinner adapter for the category selection
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category position
                int categoryPosition = spinnerCategory.getSelectedItemPosition();
                ArrayAdapter<CharSequence> unitAdapter = null;

                // Select the unit array based on the category
                switch (categoryPosition) {
                    case 0: // Length
                        unitAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.length_units, android.R.layout.simple_spinner_item);
                        break;
                    case 1: // Weight
                        unitAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.weight_units, android.R.layout.simple_spinner_item);
                        break;
                    case 2: // Temperature
                        unitAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.temperature_units, android.R.layout.simple_spinner_item);
                        break;
                }
                if (unitAdapter != null) {
                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSourceUnit.setAdapter(unitAdapter);
                    spinnerDestinationUnit.setAdapter(unitAdapter); // Set the same adapter for both spinners
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if no item is selected
            }
        });

        // On click listener for the "Convert" button
        convertButton.setOnClickListener(v -> {
            String sourceUnit = spinnerSourceUnit.getSelectedItem().toString().trim();
            String destinationUnit = spinnerDestinationUnit.getSelectedItem().toString().trim();
            String valueString = editTextValue.getText().toString();

            if (valueString.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double value = Double.parseDouble(valueString);

                // Call the conversion function
                double result = convert(sourceUnit, destinationUnit, value);

                // Show the result in the TextView
                resultTextView.setText(String.format(Locale.ENGLISH, "Converted Value: %.2f", result));
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Conversion logic for different unit categories
    public double convert(String sourceUnit, String destinationUnit, double value) {
        double result = 0.0;

        // Clean up input (remove spaces, normalize case)
        sourceUnit = sourceUnit.trim().toLowerCase();
        destinationUnit = destinationUnit.trim().toLowerCase();

        // Same unit case
        if (sourceUnit.equals(destinationUnit)) {
            return value;
        }

        // Length conversions to base unit (meter)
        double valueInMeters = 0.0;
        switch (sourceUnit) {
            case "inch": valueInMeters = value * 0.0254; break;
            case "foot": valueInMeters = value * 0.3048; break;
            case "yard": valueInMeters = value * 0.9144; break;
            case "mile": valueInMeters = value * 1609.34; break;
            case "centimeter": valueInMeters = value * 0.01; break;
            case "kilometer": valueInMeters = value * 1000; break;
        }

        switch (destinationUnit) {
            case "inch": result = valueInMeters / 0.0254; break;
            case "foot": result = valueInMeters / 0.3048; break;
            case "yard": result = valueInMeters / 0.9144; break;
            case "mile": result = valueInMeters / 1609.34; break;
            case "centimeter": result = valueInMeters / 0.01; break;
            case "kilometer": result = valueInMeters / 1000; break;
        }

        // Weight conversions to base unit (kilogram)
        double valueInKilograms = 0.0;
        switch (sourceUnit) {
            case "pound": valueInKilograms = value * 0.453592; break;
            case "ounce": valueInKilograms = value * 0.0283495; break;
            case "ton": valueInKilograms = value * 907.1847; break;
            case "kilogram": valueInKilograms = value; break;
        }

        switch (destinationUnit) {
            case "pound": result = valueInKilograms / 0.453592; break;
            case "ounce": result = valueInKilograms / 0.0283495; break;
            case "ton": result = valueInKilograms / 907.1847; break;
            case "kilogram": result = valueInKilograms; break;
        }

        // Temperature conversions using switch
        switch (sourceUnit) {
            case "celsius":
                switch (destinationUnit) {
                    case "fahrenheit":
                        result = (value * 9 / 5) + 32;
                        break;
                    case "kelvin":
                        result = value + 273.15;
                        break;
                }
                break;

            case "fahrenheit":
                switch (destinationUnit) {
                    case "celsius":
                        result = (value - 32) * 5 / 9;
                        break;
                    case "kelvin":
                        result = (value - 32) * 5 / 9 + 273.15;
                        break;
                }
                break;

            case "kelvin":
                switch (destinationUnit) {
                    case "celsius":
                        result = value - 273.15;
                        break;
                    case "fahrenheit":
                        result = (value - 273.15) * 9 / 5 + 32;
                        break;
                }
                break;
        }


        // Catch-all for unsupported conversions
        if (result == 0.0) {
            Toast.makeText(MainActivity.this, "Invalid conversion", Toast.LENGTH_SHORT).show();
        }

        return result;
    }


}

