package com.threepointogames.esk8ph.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Shader;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.locationtutorial.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.threepointogames.esk8ph.ConfirmAddress;
import com.threepointogames.esk8ph.LocalSaveData;
import com.threepointogames.esk8ph.SharedLocUser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.threepointogames.esk8ph.StringReplacer.DecodeString;
import static com.threepointogames.esk8ph.StringReplacer.EncodeString;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private DatabaseReference databaseReference;
    public DatabaseReference databaseShareLocationUsers;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private String userID;
    private String username;
    Marker newUserLocationMarker;
    List<SharedLocUser> sharedLocUsers;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    Marker userLocationMarker;
    Circle userLocationAccuracyCircle;

    ToggleButton followCameratBtn, shareLocTButton;
    Button pickChargingSpotBtn;
    protected boolean isFollowUser, isShareLocation;

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();

    private Map<String, Marker> mMarkerMap = new HashMap<>();
    public static Map<String, Marker> mChargingPointMarkerMap = new HashMap<>();
    private final static int PLACE_PICKER_REQUEST = 999;
    private final static int LOCATION_REQUEST_CODE = 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        HashMap<Integer, String> HashMap = new HashMap<Integer, String>();
        sharedLocUsers = new ArrayList<>();
        userID = LocalSaveData.loadData(MapsActivity.this, "UsersPref", "UserID");
        geocoder = new Geocoder(this);
        followCameratBtn = (ToggleButton) findViewById(R.id.followCamTButton);
        shareLocTButton = findViewById(R.id.shareLocTButton);
        pickChargingSpotBtn = findViewById(R.id.pickChargingSpotButton);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseShareLocationUsers = FirebaseDatabase.getInstance().getReference("UsersSharingLocation");
        Query dbShareLocationUsers = databaseReference.orderByChild("ShareLocation").equalTo(true);
        Query dbUserSharingLocation = databaseShareLocationUsers;


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() { // Get Other data from database
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String databaseId = snapshot.child("Id").getValue().toString();
                    String databaseUsername = snapshot.child("Username").getValue().toString();
                    if (databaseId.equals(userID)) {
                        username = databaseUsername;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dbUserSharingLocation.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String databaseId = snapshot.getValue().toString();
                    Marker previousMarker = mMarkerMap.get(databaseId);
                    if (previousMarker != null) {
                        previousMarker.remove();
                    }
                    mMarkerMap.remove(databaseId);

                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbShareLocationUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String databaseUsername = snapshot.child("Username").getValue().toString();
                    String databaseLat = snapshot.child("Location").child("latitude").getValue().toString();
                    String databaseLong = snapshot.child("Location").child("longitude").getValue().toString();
                    String databaseId = snapshot.child("Id").getValue().toString();
                    if (!databaseLat.equals("") && !databaseLong.equals("")) {

                        if (databaseId.equals(userID)) {

                        } else {

                            final LatLng latlng = new LatLng(Float.parseFloat(databaseLat), Float.parseFloat(databaseLong));
                            Marker previousMarker = mMarkerMap.get(databaseId);
                            if (previousMarker != null) {
                                //previous marker exists, update position:
                                previousMarker.setPosition(latlng);


                            } else {
                                //No previous marker, create a new one:
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(latlng)
                                        .title(databaseUsername);

                                Marker marker = mMap.addMarker(markerOptions);


                                //put this new marker in the HashMap:
                                mMarkerMap.put(databaseId, marker);
                            }
                        }


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // String databaseLongitudeString = dataSnapshot.child("Location").child("longitude").getValue().toString();

                //  setUserNewLocationMarker(Float.parseFloat(databaseLatitudeString),Float.parseFloat(databaseLongitudeString)); // Set team member location to map
            /*    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String databaseUsername = snapshot.child("Username").getValue().toString();

                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        followCameratBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    isFollowUser = true;
                } else {
                    // The toggle is disabled
                    isFollowUser = false;
                }
            }
        });

        shareLocTButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    ShareLocation();

                } else {
                    // The toggle is disabled
                    StopSharingLocation();
                }
            }
        });

        startTimer();


        // Location Picker Start Code
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

    }


    public void ShareLocation() {
        isShareLocation = true;
        databaseReference.child(userID).child("ShareLocation").setValue(isShareLocation);
        databaseShareLocationUsers.child(userID).child("Id").setValue(userID);
        databaseShareLocationUsers.child(userID).child("Username").setValue(username);
    }

    public void StopSharingLocation() {
        isShareLocation = false;
        databaseReference.child(userID).child("ShareLocation").setValue(isShareLocation);
        databaseShareLocationUsers.child(userID).removeValue();
    }
    public void AddChargingSpotMarker(LatLng longLat){
        Marker previousMarker = mChargingPointMarkerMap.get(longLat.latitude+"_"+longLat.longitude);

        if (previousMarker != null) {
            //previous marker exists, update position:
            previousMarker.setPosition(longLat);


        } else {
            //No previous marker, create a new one:
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(longLat)
                    .title(getAddress(longLat));

            Marker marker = mMap.addMarker(markerOptions);


            //put this new marker in the HashMap:
            mChargingPointMarkerMap.put(longLat.latitude+"_"+longLat.longitude, marker);
        }
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                longLat, 15);
        mMap.animateCamera(location);
    }

    public static void RemoveChargingSpotMarker(String longLat){
        Log.d("Ekko", "Remove The marker!");
        Marker previousMarker = mChargingPointMarkerMap.get(longLat);
        if (previousMarker != null) {
            previousMarker.remove();
        }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() { // Pick Charging spot location
            @Override
            public void onMapClick(LatLng latLng) {

               /* MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                markerOptions.title(getAddress(latLng));
                mMap.clear();
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15);
                mMap.animateCamera(location);
                mMap.addMarker(markerOptions);*/

                AddChargingSpotMarker(latLng);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            enableUserLocation();
//            zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }

       /* LatLng currentPosition = new LatLng(
                startPosition.latitude*(1-t)+finalPosition.latitude*t,
                startPosition.longitude*(1-t)+finalPosition.longitude*t);

        package_marker.setPosition(currentPosition);*/

        // Add a marker at Taj Mahal and move the camera
      /*  LatLng latLng = new LatLng(14.5764, 121.0851);
        MarkerOptions mymarkerOptions = new MarkerOptions()
                                            .position(latLng)
                                            .title("Taj Mahal")
                                            .snippet("Wonder of the world!");
        mMap.addMarker(mymarkerOptions);*/
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
//        mMap.animateCamera(cameraUpdate);


        //   setUserNewLocationMarker(14.5764f, 121.0851f);

       /* try {
            List<Address> addresses = geocoder.getFromLocationName("london", 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng london = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(london)
                        .title(address.getLocality());
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 16));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void setUserNewLocationMarker(float lat, float lng) {

        LatLng latLng = new LatLng(lat, lng);

        if (newUserLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar));
            //markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            newUserLocationMarker = mMap.addMarker(markerOptions);
            //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            //use the previously created marker
            newUserLocationMarker.setPosition(latLng);

        }


    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    //To start timer
    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        UpdateEk8erLocation();
                    }
                });
            }
        };
        timer.schedule(timerTask, 10000, 10000);
    }

    public void UpdateEk8erLocation() {

    }

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.redcar));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            userLocationMarker = mMap.addMarker(markerOptions);
            if (isFollowUser) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                ;
            }
        } else {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            if (isFollowUser) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                ;
            }

            if (isShareLocation) {
                //Update user location to database

                databaseReference.child(userID).child("Location").child("latitude").setValue(location.getLatitude());
                databaseReference.child(userID).child("Location").child("longitude").setValue(location.getLongitude());
            }


        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
            circleOptions.fillColor(Color.argb(32, 255, 0, 0));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            // you need to request permissions...
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        StopSharingLocation();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
//                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
       /* try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private String getAddress(LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {

                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ConfirmAddress dialogFragment = new ConfirmAddress();

            Bundle args = new Bundle();
            args.putDouble("lat", latLng.latitude);
            args.putDouble("long", latLng.longitude);
            args.putString("address", address);
            dialogFragment.setArguments(args);
            dialogFragment.show(ft, "dialog");
            return address;
        } catch (IOException e) {
            e.printStackTrace();
            return "No Address Found";

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();



                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng ltlng=new LatLng(location.getLatitude(),location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                ltlng, 16f);
                        mMap.animateCamera(cameraUpdate);
                    }
                });
                Location location = mMap.getMyLocation();



            } else {
                //We can show a dialog that permission is not granted...
            }
        }
    }


}
