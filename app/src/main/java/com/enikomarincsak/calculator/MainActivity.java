package com.enikomarincsak.calculator;

import java.text.DecimalFormat;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final String ADD = "\u002B";
	public static final String SUB = "\u2212";
	public static final String DIV = "\u00F7";
	public static final String MUL = "\u2715";
	public String value = "";
	public LinkedList<String> operators = new LinkedList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
			//getMenuInflater().inflate(R.menu.activity_main, menu); // Nincsenek beállítások ehhez az alkalmazáshoz.
        return true;
    }
    
    //Az eseménykezelőknek nyilvánosnak, érvénytelennek kell lenniük!
    public void registerKey(View view)
    {
    	switch(view.getId())
    	{
    	case R.id.button0:
    		safelyPlaceOperand("0");
    		break;
    	case R.id.button1:
    		safelyPlaceOperand("1");
    		break;
    	case R.id.button2:
    		safelyPlaceOperand("2");
    		break;
    	case R.id.button3:
    		safelyPlaceOperand("3");
    		break;
    	case R.id.button4:
    		safelyPlaceOperand("4");
    		break;
    	case R.id.button5:
    		safelyPlaceOperand("5");
    		break;
    	case R.id.button6:
    		safelyPlaceOperand("6");
    		break;
    	case R.id.button7:
    		safelyPlaceOperand("7");
    		break;
    	case R.id.button8:
    		safelyPlaceOperand("8");
    		break;
    	case R.id.button9:
    		safelyPlaceOperand("9");
    		break;
    	case R.id.buttonAdd: 
    		safelyPlaceOperator(ADD);
    		break;
    	case R.id.buttonSub:
    		safelyPlaceOperator(SUB);
    		break;
    	case R.id.buttonDiv:
    		safelyPlaceOperator(DIV);
    		break;
    	case R.id.buttonMul:
    		safelyPlaceOperator(MUL);
    		break;
    	case R.id.buttonDel:
    		deleteFromLeft();
    		break;
    	}
    	display();
    }
    
    private void display()
    {
    	TextView tvAns = (TextView) findViewById(R.id.textViewAns);
    	tvAns.setText(value);
    }
    
    private void display(String s)
    {
    	TextView tvAns = (TextView) findViewById(R.id.textViewAns);
    	tvAns.setText(s);
    }
    
    private void safelyPlaceOperand(String op)
    {
    	int operator_idx = findLastOperator();
    	// Kerülje a nullákat bizonyos esetekben (00 -> 0, 01 -> 1, 1+01 -> 1+1).
    	if (operator_idx != value.length()-1 && value.charAt(operator_idx+1) == '0')
    		deleteTrailingZero(); 
    	value += op;
    }
    
    private void safelyPlaceOperator(String op)
    {
    	if (endsWithOperator())  // Ha két műveleti jelre lett nyomva egymás után, az utolsóval számol
		{
			deleteFromLeft();
			value += op;
			operators.add(op);
		}
		else if (endsWithNumber())  // Itt már el lehet helyezni a műveleti jelet
		{
			value += op;
			operators.add(op);
		}
    	// Máskülönben: a művelet önmagában nem helyénvaló, ne helyezze el a műveleti jelet
    }
    
    private void deleteTrailingZero()
    {
    	if (value.endsWith("0")) deleteFromLeft();
    }
    
    private void deleteFromLeft()
    {
    	if (value.length() > 0)
    	{
    		if (endsWithOperator()) operators.removeLast(); 
    		value = value.substring(0, value.length()-1);
    	}
    }
    
    private boolean endsWithNumber()
    {
    	// Ha megnyomódik a pont gomb, mint tizedesvessző, tárolja el az előtte lévő számjegyeket
    	return value.length() > 0 && Character.isDigit(value.charAt(value.length()-1));
    }
     
    private boolean endsWithOperator()
    {
    	if (value.endsWith(ADD) || value.endsWith(SUB) || value.endsWith(MUL) || value.endsWith(DIV)) return true;
    	else return false;
    }
    
    private int findLastOperator()
    {
    	int add_idx = value.lastIndexOf(ADD);
    	int sub_idx = value.lastIndexOf(SUB);
    	int mul_idx = value.lastIndexOf(MUL);
    	int div_idx = value.lastIndexOf(DIV);
    	return Math.max(add_idx, Math.max(sub_idx, Math.max(mul_idx, div_idx)));
    }
    
    public void calculate(View view)  // Megjegyzés: csak a '=' gombbal hívható.
    {
    	if (operators.isEmpty()) return;  // Még nincs művelet.
    	if (endsWithOperator())
    	{
    		display("incorrect format");
    		resetCalculator();
    		return;
    	}
    		
    	// A StringTokenizer elavult. A String.split-et használja helyette
    	String[] operands = value.split("\\u002B|\\u2212|\\u00F7|\\u2715");

    	int i = 0;
    	double ans = Double.parseDouble(operands[i]); 
    	for (String operator : operators)
    		ans = applyOperation(operator, ans, Double.parseDouble(operands[++i]));

    	DecimalFormat df = new DecimalFormat("0.###");
    	display(df.format(ans));
    	resetCalculator();
    	
    }
    
    private double applyOperation(String operator, double operand1, double operand2)
    {
		// String-et switch szintaxissal nem használni, mert az Android még nem támogatja  a JDK 7-et (JRE 1.7)
    	if (operator.equals(ADD)) return operand1 + operand2;
    	if (operator.equals(SUB)) return operand1 - operand2;
    	if (operator.equals(MUL)) return operand1 * operand2;
    	if (operator.equals(DIV)) return operand1 / operand2;  // Nem kell ellenőrizni az osztást nullára, a java megteszi
    	return 0.0;
    }
    
    private void resetCalculator()
    {
    	value = "";
    	operators.clear();
    }
}
