package edu.cvtc.android.tipcalculator_gcedarblade;

import android.annotation.TargetApi;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TipCalculator extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {

    // widgets
    private EditText billAmountEditText;
    private TextView tipPercentTextView;
    private Button tipPercentUpButton;
    private Button tipPercentDownButton;
    private TextView tipAmountTextView;
    private TextView totalAmountTextView;

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

        // Listeners
        billAmountEditText.setOnEditorActionListener(this);
        tipPercentUpButton.setOnClickListener(this);
        tipPercentDownButton.setOnClickListener(this);

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
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;

        // display results to user
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipAmountTextView.setText(currency.format(tipAmount));
        totalAmountTextView.setText(currency.format(totalAmount));

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
}
