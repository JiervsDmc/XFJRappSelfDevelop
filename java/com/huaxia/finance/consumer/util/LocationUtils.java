package com.huaxia.finance.consumer.util;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.widget.Toast;

/**
 * 基于原生GPS获取地理位置
 * todo 6.0系统获取不到定位信息，可废弃
 * Created by wangjie01 on 2016/11/14.
 */
public class LocationUtils {

    private Context context;
    private LocationManager lm;
    private double latitude;
    private double longitude;

    public LocationUtils(Context context) {
        this.context = context;
    }

    /**
     * 定位位置监听器
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Location location = lm.getLastKnownLocation(provider);
            updateToNewLocation(location);
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            updateToNewLocation(null);
        }
    };

    public double[] getLocation() {
        String serviceName = Context.LOCATION_SERVICE;
        lm = (LocationManager) context.getSystemService(serviceName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);

        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = lm.getBestProvider(criteria, true);

        Location location = lm.getLastKnownLocation(provider);
        updateToNewLocation(location);
        double []  locations = new double[]{latitude,longitude};
        lm.requestLocationUpdates(provider, 10 * 1000, 30,locationListener);
        return locations;
    }

    private void updateToNewLocation(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }else {
            latitude = -1;
            longitude = -1;
        }
    }
}
