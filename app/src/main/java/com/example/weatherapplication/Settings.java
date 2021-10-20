package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onBackPressed() {
        EditText city = (EditText)findViewById(R.id.cityName);
        String cityName=city.getText().toString();

        String value="";
        RadioGroup radioTempGroup = (RadioGroup)findViewById(R.id.radioTemp);

        int radioButtonId = radioTempGroup.getCheckedRadioButtonId();
        if(radioButtonId==R.id.radioCelsius){
            value="metric";
        }else if (radioButtonId==R.id.radioFahrenheit){
            value="imperial";
        }

        //Toast.makeText(getApplicationContext(), value+"", Toast.LENGTH_SHORT).show();

        if(!cityName.equals("") || !cityName.equals(null) ){
            finish();
            Intent intent = new Intent(Settings.this,MainActivity.class);
            //.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("cityName",cityName);
            intent.putExtra("unitType",value);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Please Enter a city", Toast.LENGTH_SHORT).show();

        }



    }
}
