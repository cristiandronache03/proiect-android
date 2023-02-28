package com.example.serviciu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.LoggingMXBean;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE=100;
    private final static double LONG_PRE=-122.083d;
    private final static double LAT_PRE=37.421d;


    private SensorManager sensorManager;
    Sensor accelerometer, gyroscope;

    float[] acc = new float[3];

    TextView textV;
    Button save,show,simulare;
    Switch gpsSwitch;
    DatabaseHelper myDb;
    ProviderClient client;

    String latitude;
    String longitude;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);


        client = new ProviderClient(this);
        myDb = new DatabaseHelper(this);
        save = findViewById(R.id.save);
        show = findViewById(R.id.showBtn);
        simulare = findViewById(R.id.simularebtn);
        gpsSwitch = findViewById(R.id.gpsS);
        addSimulare();
        addSave();
        addShow();
        textV = (TextView) findViewById(R.id.text1);

        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope=sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(MainActivity.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity.this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);


    }

    private void addSave() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gpsSwitch.isChecked()) {


                    getLastLocation();

                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String strDate = formatter.format(date);

                    if (longitude != null && latitude != null) {
                        boolean reusit = myDb.insertData(strDate, longitude, latitude);
                    }

                }
                else{
                    getLastLocation();

                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    String strDate = formatter.format(date);

                    if (longitude != null && latitude != null) {
                        boolean reusit = myDb.insertDataPre(strDate, longitude, latitude);
                    }
                }
            }
        });
    }
    private void addShow() {
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gpsSwitch.isChecked()) {
                    getData();
                }
                else{
                    getDataPre();
                }
            }
        });
    }

    private void addSimulare() {
        simulare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyThread thread = new MyThread();
                thread.start();

            }
        });
    }

    private void getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    latitude = "Latitude :" +addressList.get(0).getLatitude();
                                    longitude = "Longitude :" +addressList.get(0).getLongitude();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }else{
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this,"Required Permission", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getName().contains("Gyroscope")){
            // gyroscope data
        }
        else if(sensorEvent.sensor.getName().contains("Accelerometer")){
            acc[0] = sensorEvent.values[0];
            acc[1] = sensorEvent.values[1];
            acc[2] = sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void getData(){
        Cursor c = myDb.getAllData();
        String text = "";
        while(c.moveToNext()){
            for( int i = 0; i<4 ;i ++){
                text +=c.getString(i) + "  ";
            }
            double long1 = Double.parseDouble(c.getString(2).replaceAll("[^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?]", ""));
            double lat1 = Double.parseDouble(c.getString(3).replaceAll("[^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?]", ""));
            double distance = distance(lat1, long1, LAT_PRE, LONG_PRE);
            if(distance < 0.5){
                Toast.makeText(this,"Location is close", Toast.LENGTH_LONG).show();
            }
        }
        textV.setText(text);
    }

    public void getDataPre(){
        Cursor c = myDb.getAllDataPre();
        String text = "";
        while(c.moveToNext()){
            for( int i = 0; i<4 ;i ++){
                text +=c.getString(i) + "  ";
            }
            float long1 = Float.parseFloat(c.getString(2).replaceAll("[^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?]", ""));
            float lat1 = Float.parseFloat(c.getString(3).replaceAll("[^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?]", ""));
            double distance = distance(lat1, long1, LAT_PRE, LONG_PRE);
            if(distance < 0.5){
                Toast.makeText(this,"Location is close", Toast.LENGTH_LONG).show();
            }
        }
        textV.setText(text);
    }

    public void hideData(){
        textV.setText("");
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.60934;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public class MyThread extends Thread {


        Cursor c = myDb.getAllDataPre();
        double distance;
        String text;
        public void run(){
            while(c.moveToNext()) {
                try {
                    text = "";
                    for (int i = 0; i < 4; i++) {
                        text += c.getString(i) + "  ";
                    }
                    float long1 = Float.parseFloat(c.getString(2).replaceAll("[^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?]", ""));
                    float lat1 = Float.parseFloat(c.getString(3).replaceAll("[^[+-]?(([1-9]\\d*)|0)(\\.\\d+)?]", ""));
                    distance = distance(lat1, long1, LAT_PRE, LONG_PRE);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            textV.setText(text);
                            if (distance < 0.5) {
                                Toast.makeText(MainActivity.this, "Location is close: " + distance, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}