package com.example.taxi;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ActivityPath extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private TextView tv_from, tv_fromStreet, tv_fromHouse, tv_to, tv_toStreet, tv_toHouse;
    private EditText et_fromStreet, et_fromHouse, et_toStreet, et_toHouse;
    private Button btn_ok, btn_find, btn_set;
    private String text = "";
    private LocationManager locationManager;
    private static final int REQUEST_CODE = 123;
    private static final String LANGUAGE_TAG_RU = "ru";
    private static final String FIND_LOCATION_ERROR = "Can't find location";
    private static final String TRY_AGAIN = "Try again";
    private static final String TAG = "myLogs";

    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ActivityPath: onCreate");
        setContentView(R.layout.activity_path);

        initViews();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void initViews() {
        tv_from = (TextView) findViewById(R.id.tv_from);
        tv_fromStreet = (TextView) findViewById(R.id.tv_fromStreet);
        tv_fromHouse = (TextView) findViewById(R.id.tv_fromHouse);
        tv_to = (TextView) findViewById(R.id.tv_to);
        tv_toStreet = (TextView) findViewById(R.id.tv_toStreet);
        tv_toHouse = (TextView) findViewById(R.id.tv_toHouse);
        et_fromStreet = (EditText) findViewById(R.id.et_fromStreet);
        et_fromHouse = (EditText) findViewById(R.id.et_fromHouse);
        et_toStreet = (EditText) findViewById(R.id.et_toStreet);
        et_toHouse = (EditText) findViewById(R.id.et_toHouse);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_find = (Button) findViewById(R.id.btn_find);
        btn_set = (Button) findViewById(R.id.btn_set);

        btn_ok.setOnClickListener(this);
        btn_find.setOnClickListener(this);
        btn_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
                case R.id.btn_find:
                    findLocation();
                    break;
                case R.id.btn_set:
                    Log.d(TAG, "ActivityPath: onStop");
                    setLocation();
                    break;


            case R.id.btn_ok:
                if (et_toStreet.getText().toString().isEmpty() || et_toHouse.getText().toString().isEmpty() ||
                        et_fromStreet.getText().toString().isEmpty() || et_fromHouse.getText().toString().isEmpty())
                    Toast.makeText(this, "Enter all fields!", Toast.LENGTH_SHORT).show();

                text = "Taxi will arrive to ".concat(et_fromStreet.getText().toString()).concat(", ").
                        concat(et_fromHouse.getText().toString()).concat(" in 10 minutes and take you to ").
                        concat(et_toStreet.getText().toString()).concat(", ").concat(et_toHouse.getText().toString()).
                        concat(". If you agree click CALL TAXI");
                Intent intent = new Intent();
                intent.putExtra("text", text);
                setResult(RESULT_OK, intent);
                Log.d(TAG, "ActivityPath: onStop");
                Log.d(TAG, "ActivityOutput: onStart");
                finish();

        }
    }

    private void findLocation() {
        getAddress()
                .map(address -> {
                    et_fromStreet.setText(address.getThoroughfare());
                    et_fromHouse.setText(address.getSubThoroughfare());
                    return address;
                });
    }

    private void setLocation() {
        final String[] latitude = {"0"};
        final String[] longitude = {"0"};
        getAddress().map(adr -> {
            latitude[0] = String.valueOf(adr.getLatitude());
            longitude[0] = String.valueOf(adr.getLongitude());
            return adr;
        });
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("LATITUDE", latitude[0]);
        intent.putExtra("LONGITUDE", longitude[0]);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        try {
            Address address = getAddress(
                    Double.parseDouble(data.getStringExtra("LATITUDE")),
                    Double.parseDouble(data.getStringExtra("LONGITUDE")));
            et_fromStreet.setText(address.getThoroughfare());
            et_fromHouse.setText(address.getSubThoroughfare());
        } catch (Throwable throwable) {
            Toast.makeText(this, FIND_LOCATION_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    private Optional<Address> getAddress() {
        if (!checkPermission()) {
            Toast.makeText(this, TRY_AGAIN, Toast.LENGTH_SHORT).show();
            return Optional.empty();
        }

        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Address address = getAddress(location.getLatitude(), location.getLongitude());
            return address == null ? Optional.<Address>empty() : Optional.of(address);
        }
        else{
            //This is what you need:
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            return Optional.<Address>empty();
        }
    }

    private Address getAddress (double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.forLanguageTag(LANGUAGE_TAG_RU));
        try {
            List<Address> address = geocoder.getFromLocation(latitude, longitude, 5);
            for (int i = 0; i < address.size(); i++) {
                Address adr = address.get(i);
                if (adr.getThoroughfare() != null && adr.getSubThoroughfare() != null) {
                    return adr;
                }
            }
        } catch (Throwable throwable) {
            Toast.makeText(this, FIND_LOCATION_ERROR, Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private boolean checkPermission () {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN},
                    REQUEST_CODE);
            return false;
        }
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
