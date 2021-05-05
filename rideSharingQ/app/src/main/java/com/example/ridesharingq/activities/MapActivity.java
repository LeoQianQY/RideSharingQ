package com.example.ridesharingq.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.ridesharingq.R;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private MapView mapView;

    private AMap aMap;

    LatLonPoint startPoint =  new LatLonPoint(39.810052,116.501006);

    LatLonPoint destPoint = new LatLonPoint(39.934393,116.454175);

    LatLonPoint pickupPoint =  new LatLonPoint(39.915173,116.411697);

    LatLonPoint dropoffPoint = new LatLonPoint(39.931876,116.456834);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_base);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setMapTextZIndex(2);

        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);

        final Intent intent = getIntent();
        ArrayList<String> lst = intent.getStringArrayListExtra("lst");
        if(lst.size() == 2){
            startPoint = str2Point(lst.get(0));
            destPoint = str2Point(lst.get(1));
            new Thread(() -> initSearch(mRouteSearch, startPoint, destPoint, new OnRouteSearchListener())).start();
        }
        if(lst.size() == 4){
            startPoint = str2Point(lst.get(0));
            destPoint = str2Point(lst.get(1));
            pickupPoint = str2Point(lst.get(2));
            dropoffPoint = str2Point(lst.get(3));
            new Thread(() -> initSearch(mRouteSearch, pickupPoint, dropoffPoint, new OnRouteSearchListener2())).start();
            new Thread(() -> initSearch(mRouteSearch, startPoint, pickupPoint, new OnRouteSearchListener3())).start();
            new Thread(() -> initSearch(mRouteSearch, dropoffPoint, destPoint, new OnRouteSearchListener4())).start();
        }
    }

    public LatLonPoint str2Point(String str){
        String[] lst = str.split(",");
        return new LatLonPoint(Double.parseDouble(lst[1]), Double.parseDouble(lst[0]));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private RouteSearch mRouteSearch;

    private void initSearch(RouteSearch routeSearch, LatLonPoint a, LatLonPoint b, RouteSearch.OnRouteSearchListener c) {
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(c);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(a,b);
        RouteSearch.DriveRouteQuery query = new
                RouteSearch.DriveRouteQuery(fromAndTo,RouteSearch.DRIVING_SINGLE_DEFAULT,null,null,"");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    class OnRouteSearchListener implements RouteSearch.OnRouteSearchListener{

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int rCode) {
            if (rCode == 1000){
                System.out.println(1000);
                List<DrivePath> paths = driveRouteResult.getPaths();
                List<LatLng> latLngs = new ArrayList<>();
                for (DrivePath mDrivePath : paths) {
                    for (DriveStep mDriveStep : mDrivePath.getSteps()) {
                        for (LatLonPoint mLatLonPoint : mDriveStep.getPolyline()) {
                            latLngs.add(new
                                    LatLng(mLatLonPoint.getLatitude(),mLatLonPoint.getLongitude()));
                        }
                    }
                }
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_foreground))
                        .title("Starting Point")
                        .position(new LatLng(startPoint.getLatitude(),startPoint.getLongitude())));
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_background))
                        .title("Destination")
                        .position(new LatLng(destPoint.getLatitude(),destPoint.getLongitude())));
                aMap.addPolyline(new PolylineOptions()
                                .addAll(latLngs)
                                .width(10)
                                .color(getResources().getColor(R.color.design_default_color_primary)));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < latLngs.size(); i++) {
                    builder.include(latLngs.get(i));
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }


    }
    class OnRouteSearchListener2 implements RouteSearch.OnRouteSearchListener{

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int rCode) {
            if (rCode == 1000){
                System.out.println(1000);
                List<DrivePath> paths = driveRouteResult.getPaths();
                List<LatLng> latLngs = new ArrayList<>();
                for (DrivePath mDrivePath : paths) {
                    for (DriveStep mDriveStep : mDrivePath.getSteps()) {
                        for (LatLonPoint mLatLonPoint : mDriveStep.getPolyline()) {
                            latLngs.add(new
                                    LatLng(mLatLonPoint.getLatitude(),mLatLonPoint.getLongitude()));
                        }
                    }
                }
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_foreground))
                        .title("Starting Point")
                        .position(new LatLng(pickupPoint.getLatitude(),pickupPoint.getLongitude())));
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_background))
                        .title("Pick up Point")
                        .position(new LatLng(dropoffPoint.getLatitude(),dropoffPoint.getLongitude())));
                aMap.addPolyline(new PolylineOptions()
                        .addAll(latLngs)
                        .width(10)
                        .color(getResources().getColor(R.color.design_default_color_error)));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < latLngs.size(); i++) {
                    builder.include(latLngs.get(i));
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    }

    class OnRouteSearchListener3 implements RouteSearch.OnRouteSearchListener{

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int rCode) {
            if (rCode == 1000){
                System.out.println(1000);
                List<DrivePath> paths = driveRouteResult.getPaths();

                List<LatLng> latLngs = new ArrayList<>();
                for (DrivePath mDrivePath : paths) {
                    for (DriveStep mDriveStep : mDrivePath.getSteps()) {
                        for (LatLonPoint mLatLonPoint : mDriveStep.getPolyline()) {
                            latLngs.add(new
                                    LatLng(mLatLonPoint.getLatitude(),mLatLonPoint.getLongitude()));
                        }
                    }
                }
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_foreground))
                        .position(new LatLng(startPoint.getLatitude(),startPoint.getLongitude())));
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_background))
                        .position(new LatLng(pickupPoint.getLatitude(),pickupPoint.getLongitude())));
                aMap.addPolyline(new PolylineOptions()
                        .addAll(latLngs)
                        .width(10)
                        .color(getResources().getColor(R.color.design_default_color_error)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < latLngs.size(); i++) {
                    builder.include(latLngs.get(i));
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    }

    class OnRouteSearchListener4 implements RouteSearch.OnRouteSearchListener{

        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int rCode) {
            if (rCode == 1000){
                System.out.println(1000);
                List<DrivePath> paths = driveRouteResult.getPaths();

                List<LatLng> latLngs = new ArrayList<>();

                for (DrivePath mDrivePath : paths) {
                    for (DriveStep mDriveStep : mDrivePath.getSteps()) {
                        for (LatLonPoint mLatLonPoint : mDriveStep.getPolyline()) {
                            latLngs.add(new
                                    LatLng(mLatLonPoint.getLatitude(),mLatLonPoint.getLongitude()));
                        }
                    }
                }
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_foreground))
                        .title("drop off Point")
                        .position(new LatLng(dropoffPoint.getLatitude(),dropoffPoint.getLongitude())));
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_background))
                        .title("destination Point")
                        .position(new LatLng(destPoint.getLatitude(),destPoint.getLongitude())));

                aMap.addPolyline(new PolylineOptions()
                        .addAll(latLngs)
                        .width(10)
                        .color(getResources().getColor(R.color.design_default_color_error)));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < latLngs.size(); i++) {
                    builder.include(latLngs.get(i));
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),200));
            }
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

        }
    }
}