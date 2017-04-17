package edu.cvtc.android.tipcalculator_gcedarblade;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class TipCalculator extends Activity
        implements TextView.OnEditorActionListener, SeekBar.OnSeekBarChangeListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, View.OnKeyListener {

    // widgets
    private EditText billAmountEditText;
    private TextView tipPercentTextView;

    private Button tipPercentUpButton;
    private Button tipPercentDownButton;

    private TextView tipAmountTextView;
    private TextView totalAmountTextView;

    private RadioButton roundNoneRadioButton;
    private RadioButton roundTipRadioButton;
    private RadioButton roundTotalRadioButton;

    private Spinner splitSpinner;

    private TextView perPersonLabelTextView;
    private TextView perPersonAmountLabelTextView;

    private Button applyButton;

    // shared preferences
    private SharedPreferences savedValues;

    // instance variables
    private String billAmountString = "";
    private float tipPercent = .15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tip_calculator);

        // references to widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        tipPercentTextView = (TextView) findViewById(R.id.tipPercentAmountTextView);
        tipPercentUpButton  = (Button) findViewById(R.id.tipPercentUpButton);
        tipPercentDownButton = (Button) findViewById(R.id.tipPercentDownButton);
        tipAmountTextView = (TextView) findViewById(R.id.tipAmountTextView);
        totalAmountTextView = (TextView) findViewById(R.id.totalAmountTextView);

        roundNoneRadioButton = (RadioButton) findViewById(R.id.noneRadioButton);
        roundTipRadioButton = (RadioButton) findViewById(R.id.tipRadioButton);
        roundTotalRadioButton = (RadioButton) findViewById(R.id.totalRadioButton);

        splitSpinner = (Spinner) findViewById(R.id.splitBillSpinner);

        perPersonLabelTextView = (TextView) findViewById(R.id.perPersonLabelTextView);
        perPersonAmountLabelTextView = (TextView) findViewById(R.id.perPersonAmountLabelTextView);



        // array adapter for spinner

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.split_array, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(adapter);

        // Listeners
        billAmountEditText.setOnEditorActionListener(this);
        tipPercentUpButton.setOnClickListener(this);
        tipPercentDownButton.setOnClickListener(this);
        applyButton.setOnClickListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

    }

    @Override
    public void onPause() {
        // save the instance variables
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.apply();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void calculateAndDisplay(){

        // retrieve bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        } else {
            billAmount = Float.parseFloat(billAmountString);
        }

        // calculate tip and total amount
        float tipAmount = 0;
        float totalAmount = 0;

        if (roundNoneRadioButton.isChecked()) {
            tipAmount = billAmount * tipPercent;
            totalAmount = billAmount + tipAmount;
        } else if (roundTipRadioButton.isChecked()) {
            tipAmount = StrictMath.round(billAmount * tipPercent);
            totalAmount = billAmount + tipAmount;
        } else if (roundTotalRadioButton.isChecked()) {
            float tipNotRounded = billAmount * tipPercent;
            totalAmount = StrictMath.round(billAmount + tipNotRounded);
            tipAmount = totalAmount - billAmount;
        }

        // split amount and show/hide split amount widgets
        int splitPosition = splitSpinner.getSelectedItemPosition();
        int split = splitPosition + 1;
        float perPersonAmount = 0;
        if (split == 1) {  // no split - hide widgets
            perPersonLabelTextView.setVisibility(View.GONE);
            perPersonAmountLabelTextView.setVisibility(View.GONE);
        } else { // split - show widgets
            perPersonAmount = totalAmount / split;
            perPersonLabelTextView.setVisibility(View.VISIBLE);
            perPersonAmountLabelTextView.setVisibility(View.VISIBLE);
        }

        // display results to user
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipAmountTextView.setText(currency.format(tipAmount));
        totalAmountTextView.setText(currency.format(totalAmount));
        perPersonAmountLabelTextView.setText(currency.format(perPersonAmount));

        NumberFormat percent  = NumberFormat.getPercentInstance();
        tipPercentTextView.setText(percent.format(tipPercent));

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }

        return false;

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tipPercentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();
                break;
            case  R.id.tipPercentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
