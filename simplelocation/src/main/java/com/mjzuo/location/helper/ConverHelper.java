package com.mjzuo.location.helper;

import android.location.Address;
import android.location.Location;

import com.mjzuo.location.bean.Latlng;

public class ConverHelper {

    public static Latlng loConverToLatlng(Location location) {
        if(location == null)
            return null;
        Latlng latlng = new Latlng();
        latlng.setLatitude(location.getLatitude());
        latlng.setLongitude(location.getLongitude());
        latlng.setProvider(location.getProvider());

        latlng.setBearing(location.getBearing());
        latlng.setSpeed(location.getSpeed());
        return latlng;
    }

    public static Latlng asConverToLatlng(Latlng latlng, Address address) {
        if(address == null || latlng == null)
            return null;
        latlng.setCountry(address.getCountryName());
        latlng.setCountryCode(address.getCountryCode());
        latlng.setCity(address.getAdminArea());
        latlng.setSublocality(address.getSubLocality());
        latlng.setAddress(address.getSubAdminArea()+""+address.getThoroughfare());
        latlng.setName(address.getFeatureName());
        return latlng;
    }
}
