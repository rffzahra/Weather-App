package com.example.weatherapplication;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Activity thisActivity=this;
    String cityName="Colombo";
    String unitType="metric";

    private Button btn;
    private TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            cityName = extras.getString("cityName");

            unitType = extras.getString("unitType");
        }

        FetchData fetchData = new FetchData();
        fetchData.execute();



    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch(id){

            case R.id.item2:
                startActivity(new Intent(this,Settings.class));
                break;
            case R.id.item1:
                startActivity(new Intent(this,About.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchData extends AsyncTask<String,Void,String>{
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = "";

        double lat;
        double lon;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            final String[] day_list= new String[7];
            final String [] date_list= new String[7];
            String[] status_list = new String[7];
            final String[] temp_list= new String[7];
            final Integer[] icon_list= new Integer[7];
            final String[] status_des_list= new String[7];
            final String[] humidity_list= new String[7];

            try {
                JSONObject weather_data = new JSONObject(forecastJsonStr);
                JSONArray daily = weather_data.getJSONArray("daily");

                for (int i=0;i<7;i++){
                    JSONObject date_object = daily.getJSONObject(i);
                    String dt=date_object.getString("dt");

                    long unixTimeInt=Long.parseLong(dt);
                    Date day_format=new Date(unixTimeInt*1000);
                    SimpleDateFormat simple_day_format= new SimpleDateFormat("EEEE");
                    String day = simple_day_format.format(day_format);

                    if(i==0){
                        day=day+"(Today)";
                    }
                    day_list[i]=day;

                    SimpleDateFormat simple_date_format = new SimpleDateFormat("yyyy.MM.dd");
                    String date = simple_date_format.format(day_format);
                    date_list[i]=date;

                    JSONObject temp_object = date_object.getJSONObject("temp");
                    String temp=temp_object.getString("day");
                    if(unitType.equals("metric")){
                        temp_list[i]=temp+"\u2103";
                    }else if (unitType.equals("imperial")){
                        temp_list[i]=temp+"\u2109";
                    }

                    JSONArray weather_status_Array= date_object.getJSONArray("weather");
                    JSONObject weather_status_object = weather_status_Array.getJSONObject(0);
                    String weather_status = weather_status_object.getString("main");
                    String weather_status_des= weather_status_object.getString("description");
                    status_list[i]=weather_status;
                    status_des_list[i]=weather_status_des;

                    String weather_icon = weather_status_object.getString("icon");

                    if (weather_icon.equals("01d")) {
                        icon_list[i] = R.drawable.clear_sky;
                    }else if (weather_icon.equals("02d")) {
                        icon_list[i]=R.drawable.few_clouds;
                    }else if (weather_icon.equals("03d")) {
                        icon_list[i]=R.drawable.clouds;
                    }else if (weather_icon.equals("04d")) {
                        icon_list[i]=R.drawable.clouds;
                    }else if (weather_icon.equals("09d")) {
                        icon_list[i]=R.drawable.clear_sky;
                    }else if (weather_icon.equals("10d")) {
                        icon_list[i]=R.drawable.rain;
                    }else if (weather_icon.equals("11d")) {
                        icon_list[i]=R.drawable.thunderstorm;
                    }else if (weather_icon.equals("13d")) {
                        icon_list[i]=R.drawable.snow;
                    }else if (weather_icon.equals("50d")) {
                        icon_list[i] = R.drawable.mist;
                    }

                    String humidity = date_object.getString("humidity");
                    humidity_list[i]=humidity;

                }

                CustomListAdapter adapter = new CustomListAdapter(thisActivity,day_list, icon_list, status_list,temp_list);
                ListView list = (ListView) findViewById(R.id.list_View);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String SelectedDate =date_list[+position];
                        String SelectedStatus = status_des_list[+position];
                        String SelectedHumidity =humidity_list[+position];
                        String SelectedtTemp =temp_list[+position];
                        Integer Selectedicon = icon_list[+position];
                        //Toast.makeText(getApplicationContext(),SelectedDate,Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                        intent.putExtra("ListviewclickDate",SelectedDate);
                        intent.putExtra("ListviewclickStatus",SelectedStatus);
                        intent.putExtra("ListviewclickHumidity",SelectedHumidity);
                        intent.putExtra("ListviewclickTemp",SelectedtTemp);
                        intent.putExtra("Listviewclickicon",Selectedicon);
                        intent.putExtra("Listviewclickcity",cityName);
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {



                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> address = coder.getFromLocationName(cityName,5);
                List<Double> add_list = new ArrayList<>(address.size());
                for (Address a:address){
                    if(a.hasLatitude()&&a.hasLongitude()){
                        add_list.add(a.getLatitude());
                        add_list.add(a.getLongitude());
                    }
                }

                lat=add_list.get(0);
                lon=add_list.get(1);

                final String BASE_URL ="https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=current,hourly,minutely,alerts&units="+unitType+"&appid=f970a55620a9bd001dff57d9cf1488cd";
                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) { return null; }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line1;

                while ((line1 = reader.readLine()) != null) { buffer.append(line1 + "\n"); }
                if (buffer.length() == 0) { return null; }
                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("Hi", "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) { urlConnection.disconnect(); }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Hi", "Error closing stream", e);
                    }
                }
            }
            return null;
        }


    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure want to do this?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
