package edu.cvtc.android.tipcalculator_gcedarblade;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private SeekBar percentSeekBar;

    private TextView tipAmountTextView;
    private TextView totalAmountTextView;

    private RadioGroup roundingRadioGroup;
    private RadioButton roundNoneRadioButton;
    private RadioButton roundTipRadioButton;
    private RadioButton roundTotalRadioButton;

    private Spinner splitSpinner;

    private TextView perPersonLabelTextView;
    private TextView perPersonAmountLabelTextView;

    // shared preferences
    private SharedPreferences savedValues;

    // rounding constants
    private final int ROUND_NONE = 0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;

    // instance variables
    private String billAmountString = "";
    private float tipPercent = .15f;
    private int rounding = ROUND_NONE;
    private int split = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tip_calculator);

        // references to widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        tipPercentTextView = (TextView) findViewById(R.id.tipPercentAmountTextView);
        percentSeekBar = (SeekBar) findViewById(R.id.percentSeekBar);
        tipAmountTextView = (TextView) findViewById(R.id.tipAmountTextView);
        totalAmountTextView = (TextView) findViewById(R.id.totalAmountTextView);

        roundingRadioGroup = (RadioGroup) findViewById(R.id.roundingRadioGroup);
        roundNoneRadioButton = (RadioButton) findViewById(R.id.noneRadioButton);
        roundTipRadioButton = (RadioButton) findViewById(R.id.tipRadioButton);
        roundTotalRadioButton = (RadioButton) findViewById(R.id.totalRadioButton);

        splitSpinner = (Spinner) findViewById(R.id.splitBillSpinner);

        perPersonLabelTextView = (TextView) findViewById(R.id.perPersonLabelTextView);
        perPersonAmountLabelTextView = (TextView) findViewById(R.id.perPersonAmountLabelTextView);



        // array adapter for spinner

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.split_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(adapter);

        // Listeners
        billAmountEditText.setOnEditorActionListener(this);
        billAmountEditText.setOnKeyListener(this);
        percentSeekBar.setOnSeekBarChangeListener(this);
        percentSeekBar.setOnKeyListener(this);
        roundingRadioGroup.setOnCheckedChangeListener(this);
        roundingRadioGroup.setOnKeyListener(this);
        splitSpinner.setOnItemSelectedListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

    }

    @Override
    public void onPause() {
        // save the instance variables
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.putInt("rounding", rounding);
        editor.putInt("split", split);
        editor.apply();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);
        rounding = savedValues.getInt("rounding", ROUND_NONE);
        split = savedValues.getInt("split", 1);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // set tip percent on its widget
        int progress = Math.round(tipPercent * 100);
        percentSeekBar.setProgress(progress);

        // set rounding on radio buttons
        if (rounding == ROUND_NONE) {
            roundNoneRadioButton.setChecked(true);
        } else if (rounding == ROUND_TIP) {
            roundTipRadioButton.setChecked(true);
        } else if (rounding == ROUND_TOTAL) {
            roundTotalRadioButton.setChecked(true);
        }

        // set split on spinner
        int position = split - 1;
        splitSpinner.setSelection(position);

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

        // get tip percent
        int progress = percentSeekBar.getProgress();
        tipPercent = (float) progress / 100;

        // calculate tip and total amount
        float tipAmount = 0;
        float totalAmount = 0;

        if (rounding == ROUND_NONE) {
            tipAmount = billAmount * tipPercent;
            totalAmount = billAmount + tipAmount;
        } else if (rounding == ROUND_TIP) {
            tipAmount = StrictMath.round(billAmount * tipPercent);
            totalAmount = billAmount + tipAmount;
        } else if (rounding == ROUND_TOTAL) {
            float tipNotRounded = billAmount * tipPercent;
            totalAmount = StrictMath.round(billAmount + tipNotRounded);
            tipAmount = totalAmount - billAmount;
        }

        // calculate the split amount and show/hide split amount widgets
        float splitAmount = 0;
        if (split == 1) {
            perPersonLabelTextView.setVisibility(View.GONE);
            perPersonAmountLabelTextView.setVisibility(View.GONE);
        } else {
            splitAmount = totalAmount / split;
            perPersonAmountLabelTextView.setVisibility(View.VISIBLE);
            perPersonAmountLabelTextView.setVisibility(View.VISIBLE);
        }

        // display results to user
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipAmountTextView.setText(currency.format(tipAmount));
        totalAmountTextView.setText(currency.format(totalAmount));
        perPersonAmountLabelTextView.setText(currency.format(splitAmount));

        NumberFormat percent  = NumberFormat.getPercentInstance();
        tipPercentTextView.setText(percent.format(tipPercent));

    }


    // event handler for EditText
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }

        return false;

    }

    // event handler for SeekBar
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tipPercentTextView.setText(progress + "&");

    }



    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        calculateAndDisplay();

    }

    // event handler for the RadioGroup

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        switch(checkedId) {
            case R.id.noneRadioButton:
                rounding = ROUND_NONE;
                break;
            case R.id.tipRadioButton:
                rounding = ROUND_TIP;
                break;
            case R.id.totalRadioButton:
                rounding = ROUND_TOTAL;
                break;
        }

        calculateAndDisplay();

    }

    // event handler for the keyboard and DPad
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                calculateAndDisplay();
                imm.hideSoftInputFromWindow(billAmountEditText.getWindowToken(), 0);
                return true;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                calculateAndDisplay();
                imm.hideSoftInputFromWindow(billAmountEditText.getWindowToken(), 0);
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (view.getId() == R.id.percentSeekBar) {
                    calculateAndDisplay();
                }
                break;

        }
        return false;

    }

    // event handler for the Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        split = position + 1;
        calculateAndDisplay();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }




}
