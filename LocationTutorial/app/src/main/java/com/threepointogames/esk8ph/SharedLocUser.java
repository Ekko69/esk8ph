package com.threepointogames.esk8ph;

import com.example.locationtutorial.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SharedLocUser {
    private GoogleMap mMap;
    public String id;
    public float latitude;
    public float longitude;
    public Marker newUserLocationMarker;

    public SharedLocUser(String _id,float _latitude,float _longitude){
        id =_id;
        latitude=_latitude;
        longitude = _longitude;

    }

    private void setLocationMarker(float lat, float lng) {

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

    public SharedLocUser(String _id){
        id=_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
