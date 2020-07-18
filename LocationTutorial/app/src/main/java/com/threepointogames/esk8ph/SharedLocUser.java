package com.threepointogames.esk8ph;

public class SharedLocUser {
    public String id;
    public float latitude;
    public float longitude;

    public SharedLocUser(String _id,float _latitude,float _longitude){
        id =_id;
        latitude=_latitude;
        longitude = _longitude;

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
