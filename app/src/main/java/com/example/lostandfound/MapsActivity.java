package com.example.lostandfound;

import static java.lang.Double.parseDouble;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.lostandfound.data.DatabaseHelper;
import com.example.lostandfound.model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lostandfound.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHelper(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        LatLng deakin = new LatLng(-37.847169, 145.114941);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.addMarker(new MarkerOptions().position(deakin).title("Marker in Deakin"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(deakin));
//    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("On map ready", String.valueOf(db.getCountOfInvalidLocations()));
        mMap = googleMap;
        List<Post> posts = db.getAllPosts();

        for (Post post : posts) {
            double latitude = 0.0;
            double longitude = 0.0;

            if (post.getLatitude() != null && post.getLongitude() != null) {
                try {
                    latitude = Double.parseDouble(post.getLatitude());
                    longitude = Double.parseDouble(post.getLongitude());
                } catch (NumberFormatException e) {
                    Log.e("catch","not converted to double");
                    e.printStackTrace();
                }
            }
            if (latitude != 0.0 && longitude != 0.0) {
                LatLng postLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(postLocation).title(post.getPostName()));
            }
        }
        if (!posts.isEmpty()) {
            Post lastPost = posts.get(posts.size() - 1);
            double lastLatitude = 0.0;
            double lastLongitude = 0.0;

            if (lastPost.getLatitude() != null && lastPost.getLongitude() != null) {
                try {
                    lastLatitude = Double.parseDouble(lastPost.getLatitude());
                    lastLongitude = Double.parseDouble(lastPost.getLongitude());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            if (lastLatitude != 0.0 && lastLongitude != 0.0) {
                LatLng lastPostLocation = new LatLng(lastLatitude, lastLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPostLocation, 10f));
            }
        }

    }
}
