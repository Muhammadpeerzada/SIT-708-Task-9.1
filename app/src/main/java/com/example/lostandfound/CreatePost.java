package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class CreatePost extends AppCompatActivity {
    DatabaseHelper db;
    EditText postLocation;
    ProgressBar progressBar;
    String latitude;
    String longitude;
    String placeName;
    LocationListener locationListener;
    LocationManager locationManager;
    AutocompleteSupportFragment autocompleteFragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (locationManager != null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    } else {
                        Log.e("CreatePost", "LocationManager is null");
                    }
                } else {
                    Log.e("CreatePost", "Location permission not granted");
                }
            }
        }
    }

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        EditText postName = findViewById(R.id.et_name);
        EditText phoneNumber = findViewById(R.id.et_phone);
        EditText postDescription = findViewById(R.id.multi_tv_description);
        EditText postDate = findViewById(R.id.et_date);

        progressBar = findViewById(R.id.progressBar);
        RadioGroup radioGroup = findViewById(R.id.radio_group);
        Button createPost = findViewById(R.id.create_post);
        Button getCurrentLocation = findViewById(R.id.get_location);
        db = new DatabaseHelper(this);
        Places.initialize(getApplicationContext(), "AIzaSyDaBGKsMkqqyxN4RNt2vil6D0mUNUeI8co");
        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.et_location);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    latitude = String.valueOf(latLng.latitude);
                    longitude = String.valueOf(latLng.longitude);
                }
                placeName = place.getName();
                autocompleteFragment.setText(place.getName());
                Log.e("onPlaceSelected", "Place: " + place.getName() + ", " + latitude + ", " +longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("OnError", "An error occurred: " + status);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                autocompleteFragment.setText(latitude + ", " + longitude);
            }
        };

        getCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                if (locationManager != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        latitude = String.valueOf(lastKnownLocation.getLatitude());
                        longitude = String.valueOf(lastKnownLocation.getLongitude());
                        reverseGeocode();
                    } else {
                        postLocation.setError("Unable to get location");
                        Log.e("CreatePost", "Last known location is null");
                    }
                } else {
                    Log.e("CreatePost", "LocationManager is null");
                }
            }
        });

        postDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePost.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.forLanguageTag("en-AU"), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year);
                        postDate.setText(selectedDate);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        createPost.setOnClickListener(view -> {
            String name = postName.getText().toString();
            String phone = phoneNumber.getText().toString();
            String description = postDescription.getText().toString();
            String state = getState(radioGroup);
            String date = postDate.getText().toString();
            String location = placeName;
            String result = "";
            if (name.equals(""))
                postName.setError("Please enter name");
            else if (phone.equals(""))
                phoneNumber.setError("Please enter phone number");
            else if (description.equals(""))
                postDescription.setError("Please enter description");
            else if (date.equals(""))
                postDate.setError("Please enter date");
            else if (location.equals(""))
                Log.i("not selected location", "PLease select location");
            else
                result = createPost(name, phone, description, state, date, location, latitude, longitude);
            Toast.makeText(CreatePost.this, result, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private String getState(RadioGroup radioGroup) {
        String selectedState;
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_lost)
            selectedState = "Lost";
        else
            selectedState = "Found";
        return selectedState;
    }

    private String createPost(String name, String phone, String description, String state, String date, String location, String latitude, String longitude) {
        if (latitude == null || latitude.equals("0") || longitude == null || longitude.equals("0")) {
            return "Invalid location. Please select a valid location.";
        }
        long result = db.insertPost(new Post(name, phone, description, state, date, location, latitude, longitude));
        if (result > 0) {
            return "Post has been created successfully!";
        } else {
            return "There was an error creating the post.";
        }
    }


    private void reverseGeocode() {
        String apiKey = "AIzaSyDaBGKsMkqqyxN4RNt2vil6D0mUNUeI8co";
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + apiKey;

        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                URL geocodeUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) geocodeUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String response = stringBuilder.toString();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray results = jsonObject.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject firstResult = results.getJSONObject(0);
                            String formattedAddress = firstResult.getString("formatted_address");

                            runOnUiThread(() -> {
                                autocompleteFragment.setText(formattedAddress);
                                placeName = formattedAddress;
                                Log.e("ReverseGeocode", "Formatted Address: " + formattedAddress);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ReverseGeocode", "Error: " + responseCode);
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        }).start();
    }
}
