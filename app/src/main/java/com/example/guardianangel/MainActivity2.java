package com.example.guardianangel;
//130613
import static android.Manifest.permission.CALL_PHONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
    Button b1,b2;
    private FusedLocationProviderClient client;
    DBHandler mydbHandler;
    private final int REQUEST_CHECK_CODE = 8989;
    private LocationSettingsRequest.Builder builder;
    String x;
    String y;
    private static final int REQUEST_LOCATION = 1;

    LocationManager locationManager;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        b1 = findViewById(R.id.addContact);
        b2 = findViewById(R.id.emergencies);
        mydbHandler = new DBHandler(MainActivity2.this);

        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(),R.raw.police_siren);

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            onGPS();
        }else{
            Toast.makeText(this, "Control going to startTrack()", Toast.LENGTH_SHORT).show();
            startTrack();
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
            }
        });
        b2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                mp.start();
                loadData();

                Toast.makeText(MainActivity2.this, "PANIC BUTTON STARTED", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void loadData(){
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = mydbHandler.getListContents();
        if(data.getCount()==0) {
            Toast.makeText(this, "NO CONTENT", Toast.LENGTH_SHORT).show();
        }else{
            String msg = "I NEED HELP, LATITUDE: " + x + " LONGITUDE : " + y;
            String number = "";

            while (data.moveToNext()){
                theList.add(data.getString(1));
                number = number + data.getString(1) + (data.isLast()?"":";");
                call(number);
            }
            if (!theList.isEmpty()){
                sendSms(number,msg,true);
                Toast.makeText(MainActivity2.this, msg, Toast.LENGTH_SHORT).show();

                Toast.makeText(this, "MSG SENT TO" + number, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("IntentReset")
    private void sendSms(String number, String msg, boolean b) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,msg+address,null,null);
//
//        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setData(Uri.parse("smsto:"));
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.putExtra("address", number);
//        smsIntent.putExtra("sms_body: ",msg);
//        startActivity(smsIntent);

        }

    private void call(String number) {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+number));
        if(ContextCompat.checkSelfPermission(getApplicationContext(),CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            startActivity(i);
        }else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{CALL_PHONE},1);
            }
        }

    }

    public String address="";

    private void startTrack() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity2.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                x = String.valueOf(lat);
                y = String.valueOf(longi);

            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }

//        if(ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(MainActivity2.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{
//                    Manifest.permission.ACCESS_FINE_LOCATION
//            },REQUEST_LOCATION);
//        }
//        else{
//            Toast.makeText(this, "Control in else statement of track", Toast.LENGTH_SHORT).show();
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
//                @Override
//                public void onLocationChanged(@NonNull Location location) {
//                    try {
//                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//                        address = addresses.get(0).getAddressLine(0);
//                        x = (location.getLatitude());
//                        y = (location.getLongitude());
//                        Toast.makeText(MainActivity2.this, "X : Y : ", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        Toast.makeText(MainActivity2.this, "ERROR IN ELSE OF TRACK", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if(locationGPS!=null){
//                double lat = locationGPS.getLatitude();
//                double lon = locationGPS.getLongitude();
//                x = String.valueOf(lat);
//                y = String.valueOf(lon);
//            }else{
//                Toast.makeText(this, "UNABLE TO FIND LOCATION" + locationGPS, Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}