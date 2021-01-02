package com.swayam.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String mainString = "";
    private String answerString = "0";
    private TextView answerView,hintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        answerView = findViewById(R.id.answer);
        hintView = findViewById(R.id.hintAnswer);

    }

    public void onNumberClick(View view){
        if (view.getTag().toString().equals(".")){
            if (mainString.equals(""))
                return;
            for (int i=mainString.length()-1;i>=0;i--){
                char c = mainString.charAt(i);
                if (c == '.'){
                    return;
                }
                if (c == '+' || c == '-' || c == 'x' || c == '/'){
                    break;
                }
            }
            if (!mainString.substring(mainString.length()-1,mainString.length()).matches("[0-9]")){
                return;
            }else {
                mainString = mainString + view.getTag();
            }
        }else if (mainString.length() == 2 && mainString.equals("-0")){
            if (view.getTag().equals("0"))
                return;
            else
                mainString = "-" + view.getTag();
        }else {
            if (mainString.length() == 1 && mainString.charAt(0)=='0'){
                mainString = view.getTag().toString();
            }else {
                mainString = mainString + view.getTag();
            }
        }
        updateViews();
    }

    public void onOperatorClick(View view) {
        String tag = view.getTag().toString();
        if (tag.equals("-")) {
            if (mainString.length() == 1 && mainString.substring(mainString.length() - 1, mainString.length()).matches("[+/x-]")){
                return;
            }
            if (mainString.length() >= 2 &&
                    mainString.substring(mainString.length() - 1, mainString.length()).matches("[+/x-]") &&
                    mainString.substring(mainString.length() - 2, mainString.length()-1).matches("[+/x-]")) {
                return;
            }
        }else if (mainString.equals("")){
            return;
        }else if (mainString.length() == 1 && mainString.contains("-")){
            tag = "";
            mainString = "";
        }else if (mainString.substring(mainString.length()-1,mainString.length()).matches("[+/x-]")){
            return;
        }
        mainString = mainString + tag;
        updateViews();
    }

    public void onEqualClick(View view){
        try {
            double answer = getAnswer(mainString);
            if (answer % 1 == 0)
                answerString = "" + (long)answer;
            else
                answerString = "" + answer;
            mainString = answerString;
            answerView.setText(answerString);
        }catch (Exception e){
            answerView.setText("syntax error");
            answerString = "0";
        }
    }

    public void onCancelOrDeleteClick(View view){
        switch (view.getTag().toString()){
            case "C":

                mainString = "";
                answerString = "0";

                break;
            case "D":

                if (mainString.equals("") || mainString.length() == 1){
                    mainString = "";
                    answerString = "0";
                    updateViews();
                    return;
                }else {
                    mainString = mainString.substring(0,mainString.length()-1);
                }

                break;
        }
        updateViews();
    }

    private void updateViews(){
        mainString = mainString.replace("s","-");
        answerView.setText(answerString);
        hintView.setText(mainString);
    }

    private double getAnswer(String string) throws Exception{
        Exception e = new Exception("syntax error!");
        Double answer = 0.0;
        if (string.equals("")){
            return answer;
        }

        if (!string.matches("[-]?[0-9]+[.]?[0-9]*([+-/x][-]?[0-9]+[.]?[0-9]*)*")){
            throw e;
        }

        string = string.replace("--","s-").replaceAll("([0-9])[-]([0-9])","$1s$2");

        Log.i("TAG", "getAnswer: "+string);

        answer = calculate(string);

        return answer;
    }

    private double calculate(String string){
        String TAG = "CALCULATE";
        char[] chars = new char[]{'/','x','+','s'};
        for (char c : chars){
            if (string.contains(String.valueOf(c))){
                String prev = "";
                int prevIndex = 0,nextIndex = 0;
                int index  = string.indexOf(c);
                for (int i=index-1;i>=0;i--){
                    prevIndex = i;
                    if (String.valueOf(string.charAt(i)).matches("[+/xs]")){
                        prevIndex = prevIndex + 1;
                        break;
                    }else {
                        prev = string.charAt(i) + prev;
                    }
                }
                double prevValue = Double.parseDouble(prev);

                String next = "";
                for (int i=index+1;i<string.length();i++){
                    nextIndex = i + 1;
                    if (String.valueOf(string.charAt(i)).matches("[+/xs]")){
                        nextIndex = i;
                        break;
                    }else {
                        next = next + string.charAt(i) ;
                    }
                }
                double nextValue = Double.parseDouble(next);

                double answer = 0.0;
                switch (c){
                    case '/':
                        answer = prevValue / nextValue;
                        break;
                    case 'x':
                        answer = prevValue * nextValue;
                        break;
                    case '+':
                        answer = prevValue + nextValue;
                        break;
                    case 's':
                        answer = prevValue - nextValue;
                        break;
                }

                String val = string.replace(string.substring(prevIndex,nextIndex),""+answer);
                Log.i(TAG, "calculate: "+ val);
                return calculate(val);
            }
        }
        return Double.parseDouble(string);
    }
}