package edu.cvtc.android.tipcalculator_gcedarblade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TipCalculator extends AppCompatActivity {

    // widgets
    private EditText billAmountEditText;
    private TextView tipPercentTextView;
    private Button tipPercentUpButton;
    private Button tipPercentDownButton;
    private TextView tipAmountTextView;
    private TextView totalAmountTextView;

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
    }
}
