package com.example.multiplemarker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.multiplemarker.data.model.nearby.ModelResult;
import com.example.multiplemarker.databinding.ActivityMainBinding;
import com.example.multiplemarker.viewmodel.MainViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    int REQ_PERMISSION = 1;
    double strCurrentLat;
    double strCurrentLng;
    GoogleMap mapView;
    String strCurrentLocation, strKeyword;
    SimpleLocation simpleLocation;
    MainViewModel mainViewModel;
    SupportMapFragment supportMapFragment;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setContentView(R.layout.activity_main);

        //set transparent statusbar
        setStatusBar();

        //show permission
        showPermission();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("sedang menampilkan lokasi");

        simpleLocation = new SimpleLocation(this);
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this);
        }

        //get location
        strCurrentLat = simpleLocation.getLatitude();
        strCurrentLng = simpleLocation.getLongitude();



        //set location lat long
        strCurrentLocation = strCurrentLat + "," + strCurrentLng;
        Log.d(TAG, "onCreate: currentlocatocation" + strCurrentLocation);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(binding.mapFragment.getId());
        supportMapFragment.getMapAsync(this);

        binding.imageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchLocation.getText().clear();
                binding.imageClear.setVisibility(View.GONE);
                mapView.clear();
            }
        });

        binding.searchLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                strKeyword = binding.searchLocation.getText().toString();
                if (strKeyword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Form tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                } else {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        progressDialog.show();
                        mainViewModel.setMarkerLocation(strCurrentLocation, strKeyword);
                        InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        binding.imageClear.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void showPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION);
        }
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PERMISSION && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: ");
            //load viewmodel
            getLocationViewModel(strKeyword);
        }

    }

    private void getLocationViewModel(String strKeyword) {
        mainViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        mainViewModel.getMarkerLocation().observe(this, modelResults -> {
            if (modelResults.size() != 0) {

                //get multiple marker
                getMarker(modelResults);
                progressDialog.dismiss();
            } else {
                Toast.makeText(this, "Oops, tidak bisa mendapatkan lokasi kamu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMarker(ArrayList<ModelResult> modelResultsArrayList) {
        for (int i = 0; i < modelResultsArrayList.size(); i++) {

            //set LatLong from API
            LatLng latLngMarker = new LatLng((
                    modelResultsArrayList.get(i)
                            .getModelGeometry()
                            .getModelLocation()
                            .getLat()), (
                    modelResultsArrayList.get(i)
                            .getModelGeometry()
                            .getModelLocation()
                            .getLng()));

            //get LatLong to Marker
            mapView.addMarker(new MarkerOptions()
                    .position(latLngMarker)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(modelResultsArrayList.get(i).getName()));

            //show Marker
            LatLng latLngResult = new LatLng((
                    modelResultsArrayList.get(0).getModelGeometry()
                            .getModelLocation()
                            .getLat()), (
                    modelResultsArrayList.get(0)
                            .getModelGeometry()
                            .getModelLocation()
                            .getLng()));

            //set position marker
            mapView.moveCamera(CameraUpdateFactory.newLatLng(latLngResult));
            mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngResult.latitude, latLngResult.longitude), 14));
            mapView.getUiSettings().setAllGesturesEnabled(true);
            mapView.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mapView = googleMap;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(strCurrentLat, strCurrentLng, 1);
            if (addressList != null && addressList.size() > 0) {
                String strCurrentLocation = addressList.get(0).getSubLocality();
                String strAddress = addressList.get(0).getAddressLine(0);
                binding.tvCurrentLocation.setText(strCurrentLocation);
                binding.tvAddress.setText(strAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng sydney = new LatLng(strCurrentLat, strCurrentLng);
        mapView.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mapView.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getLocationViewModel(strKeyword);
    }


}