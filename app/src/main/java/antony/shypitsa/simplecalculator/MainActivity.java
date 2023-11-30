package antony.shypitsa.simplecalculator;

import static java.lang.Math.*;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    private EditText editText;
    private Button buttonAC;
    Character operator = '\0';
    BigDecimal oldNumber;//первое число
    BigDecimal result = new BigDecimal("0.0");
    Boolean firstNull = true;
    DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
    DecimalFormat df = new DecimalFormat("#.#########", decimalFormatSymbols);
    DecimalFormat dfForLongStr = new DecimalFormat("#.#####E0", decimalFormatSymbols);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editText);
        buttonAC = findViewById(R.id.buttonAC);
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_click);
    }
    private void firstNullChecker()
    {
        if (firstNull) {
            editText.setText("");
            firstNull = false;
        }
    }
    private Boolean dotIsNotFirst(String number){
        return number.contains(".");
    }
    private Boolean minusIsPresent(String number){
        return !number.isEmpty() && number.charAt(0) == '-';
    }
    private Boolean numberIsZero(String number) {
        return number.equals("0") || number.equals("");
    }
    public void clickNumber(View view) {
        mediaPlayer.start();
        firstNullChecker();//убирает начальный ноль
        buttonAC.setText(R.string.c);
        String number = editText.getText().toString();
        if(view.getId() == R.id.button1) {
            number += "1";
        } else if (view.getId() == R.id.button2) {
            number += "2";
        }else if (view.getId() == R.id.button3) {
            number += "3";
        }else if (view.getId() == R.id.button4) {
            number += "4";
        }else if (view.getId() == R.id.button5) {
            number += "5";
        }else if (view.getId() == R.id.button6) {
            number += "6";
        }else if (view.getId() == R.id.button7) {
            number += "7";
        }else if (view.getId() == R.id.button8) {
            number += "8";
        }else if (view.getId() == R.id.button9) {
            number += "9";
        }else if (view.getId() == R.id.button0) {
            if(zeroIsFirst(number)){
                number = "0";
                buttonAC.setText(R.string.ac);
                firstNull = true;
            }else{
                number += "0";
            }
        } else if (view.getId() ==  R.id.buttonDot) {
            if(dotIsNotFirst(number)){
                Toast.makeText(MainActivity.this, "Повторный ввод точки запрещен", Toast.LENGTH_SHORT).show();
            }else{
                if(number.isEmpty()){
                    number = "0.";
                }else {
                    number += ".";
                }
            }
        } else if (view.getId() == R.id.buttonPlusMinus) {
            if (!numberIsZero(number)){
                if(minusIsPresent(number)){
                    number = number.substring(1);
                }else{
                    number = "-" + number;
                }
            }else{
                number = "0";
                firstNull = true;
            }
        }
        editText.setText(number);
    }
    private Boolean zeroIsFirst(String number) {
        return number.equals("");
    }
    public void clickOperation(View view) {
        mediaPlayer.start();
        firstNull = true;
        if(!editText.getText().toString().equals(operator.toString())){
            oldNumber = new BigDecimal(editText.getText().toString());
        }
        editText.setText("");
        if(view.getId() == R.id.buttonPlus) {
            operator = '+';
        } else if (view.getId() == R.id.buttonMinus) {
            operator = '-';
        }else if (view.getId() == R.id.buttonMultiply) {
            operator = '*';
        }else if (view.getId() == R.id.buttonDivide) {
            operator = '/';
        }
        editText.setText(operator.toString());
    }
    public void clickEqual(View view) {
        mediaPlayer.start();
        BigDecimal newNumber;
        try {
            newNumber = new BigDecimal(editText.getText().toString());
        }catch(NumberFormatException e){
            result = oldNumber;
            editText.setText(df.format(result));
            return;
        }
        switch (operator){
            case '+': result = oldNumber.add(newNumber); break;
            case '-': result = oldNumber.subtract(newNumber); break;
            case '*': result = oldNumber.multiply(newNumber); break;
            case '/':
                if(numberIsZero(newNumber.toString())){
                    Toast.makeText(MainActivity.this,"На ноль делить нельзя!", Toast.LENGTH_SHORT).show();
                    editText.setText("0");
                    return;
                }else{
                    result = oldNumber.divide(newNumber, 9, RoundingMode.HALF_UP);//округляет значение к ближайшему числу, при равенстве значений округляет вверх
                }
                break;
            case '\0': result = newNumber; break;
        }
        String resultString = result.toPlainString();// Преобразование BigDecimal в строку без экспоненциальной формы
        int dotIndex = resultString.indexOf('.');
        if (dotIndex != 1 & resultString.substring( dotIndex + 1).length() > 9)
        {
            editText.setText(dfForLongStr.format(result));
        }else{
            editText.setText(df.format(result));
        }
        operator = '\0';
        firstNull = true;
    }
    public void clickAC(View view) {
        mediaPlayer.start();
        firstNull = true;
        editText.setText("0");
        buttonAC.setText(R.string.ac);
    }
    public void clickPercent(View view) {
        mediaPlayer.start();
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal newNumber;
        if (operator == '\0') {
            BigDecimal number = new BigDecimal(editText.getText().toString());
            result = number.divide(hundred);
            editText.setText(String.valueOf(result));
        } else {
            try {
                newNumber = new BigDecimal(editText.getText().toString());
            }catch (NumberFormatException e){
                editText.setText(df.format(oldNumber));
                operator = '\0';
                return;
            }
            switch (operator) {
                case '+':
                    result = oldNumber.add(oldNumber.multiply(newNumber.divide(hundred)));
                    break;
                case '-':
                    result = oldNumber.subtract(oldNumber.multiply(newNumber.divide(hundred)));

                    break;
                case '*':
                    result = oldNumber.multiply(newNumber.divide(hundred));
                    break;
                case '/':
                    result = oldNumber.divide(newNumber.divide(hundred), BigDecimal.ROUND_HALF_UP);
                    break;
            }
            editText.setText(df.format(result));
            operator = '\0';
        }
        firstNull = true;
    }
    public void clickMathFunc(View view) {
        mediaPlayer.start();
        String number = editText.getText().toString();
        double angelToRadian = (PI / 180) * Double.parseDouble(number);
        double result = 0.0;
        if(view.getId() == R.id.buttonSin){
            result = sin(angelToRadian);
        } else if (view.getId() == R.id.buttonCos) {
            result = cos(angelToRadian);
        } else if (view.getId() == R.id.buttonTg) {
            result = tan(angelToRadian);
        } else if (view.getId() == R.id.buttonExp) {
            result = exp(1);
        }
        editText.setText(df.format(result));
        firstNull = true;
    }
}