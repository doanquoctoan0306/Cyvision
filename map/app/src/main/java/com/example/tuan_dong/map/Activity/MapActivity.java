package com.example.tuan_dong.map.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuan_dong.map.Database.Address;
import com.example.tuan_dong.map.Database.AddressModify;
import com.example.tuan_dong.map.R;
import com.example.tuan_dong.map.Routes.DirectionFinder;
import com.example.tuan_dong.map.Routes.Route;
import com.example.tuan_dong.map.Routes.Step;
import com.example.tuan_dong.map.Utils.DirectionFinderListener;
import com.example.tuan_dong.map.Utils.MainMenuUtils;
import com.example.tuan_dong.map.Utils.MapHandleUtils;
import com.example.tuan_dong.map.Utils.TextToSpeechUtils;
import com.example.tuan_dong.map.listener.CompassSensorListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static Location TEMP_LOCATION;
    private static final float DEFAULT_ZOOM = 17.5f;
    private static final int MIN_DURATION_REQUEST_LOCATION = 2000; // 2s
    private static final double THRESHOLD_TO_POINT = 15;

    public GoogleMap m_map;

    private Boolean m_locationPermissionsGranted = false;
    private Marker m_marker;
    ArrayList<Address> m_list = new ArrayList<Address>();
    private List<Marker> m_originMarkers = new ArrayList<>();
    private List<Marker> m_destinationMarkers = new ArrayList<>();
    private List<Polyline> m_polylinePaths = new ArrayList<>();
    private LocationManager m_manager;
    private Location m_current;
    private List<Route> m_directedRoute;
    private LatLng m_des;
    private FusedLocationProviderClient m_fusedLocationProviderClient;
    private LocationListener m_locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //get the latitude and longitude from the location
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            TEMP_LOCATION = location;
            updateMarker(latitude, longitude);
            guide(latitude, longitude);
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
    };

    // used to prevent repeat instruction of a step
    private Step m_curretStep;
    private int m_isStartToGo;
    private CompassSensorListener m_compassSensorListener;
    private SensorManager m_sensorMng;
    private Sensor m_accelerometer;
    private Sensor m_magnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        AddressModify addressModify = new AddressModify(MapActivity.this);
        m_list = addressModify.getPlaceArrayList();
        m_manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        m_directedRoute = new ArrayList<>();
        m_isStartToGo = 0;

        initMap();
        handleIntentAction();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (m_sensorMng == null) {
            initSensor();
        }
        m_sensorMng.registerListener(m_compassSensorListener, m_accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        m_sensorMng.registerListener(m_compassSensorListener, m_magnetometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    private void initSensor() {
        m_sensorMng = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_accelerometer = m_sensorMng.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_magnetometer = m_sensorMng.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        m_compassSensorListener = new CompassSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_sensorMng.unregisterListener(m_compassSensorListener);
    }

    /**
     * get intent to know whether should we go to find direction
     */
    private void handleIntentAction() {
        Intent intent = getIntent();
        if (intent == null)
            return;

        String action = intent.getAction();
        if (action == null)
            return;

        switch (action) {
            case MainActivity.ACTION_DIRECTION_TO_PLACE:
                if (!m_locationPermissionsGranted || m_current == null)
                    return;
                double latitude = intent.getDoubleExtra(MainActivity.EXTRA_LATITUDE, -1);
                double longitude = intent.getDoubleExtra(MainActivity.EXTRA_LONGITUDE, -1);
                m_des = new LatLng(latitude, longitude);
                if (latitude == -1 || longitude == -1) {
                    TextToSpeechUtils.speak(MapActivity.this, TextToSpeechUtils.TEXT_REQ_DIRECTION_FAIL);
                    startActivity(new Intent(MapActivity.this, MainActivity.class));
                    return;
                }
                this.sendRequestGoFromHereToPlace(latitude, longitude);
                return;
            case "map_exception":
                TextView textView = (TextView) findViewById(R.id.instructionText);
                textView.setText(intent.getStringExtra("trace"));
                return;
            case MainActivity.ACTION_CANCEL_DIRECTION:
                this.cancelDirection(null);
                break;
            case MainActivity.ACTION_COMEBACK_DIRECTION:
                this.directToGoBack(null);
                break;
        }
    }

    private void updateMarker(double latitude, double longitude) {
        LatLng locations = new LatLng(latitude, longitude);
        if (m_marker != null) {
            m_marker.remove();
        }
        m_marker = m_map.addMarker(new MarkerOptions()
                //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_navigation))
                .position(locations)
                .flat(true)
                .rotation(77)
                .title("Vị Trí Hiện Tại"));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(locations)
                .zoom(17)
                .bearing(90)
                .build();
        m_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);
    }

    private void setMyLocation() {
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

        m_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_DURATION_REQUEST_LOCATION, 1, m_locationListener);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    //gui cac toa do de tim duong di
    private void sendRequest(String origin, String destination) {
        try {
            new DirectionFinder(MapActivity.this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendRequestGoFromHereToPlace(double des_latitude, double des_longitude) {
        this.sendRequest(m_current.getLatitude() + "," + m_current.getLongitude(), des_latitude + "," + des_longitude);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MainMenuUtils.declareMainMenu(this, id);

        return super.onOptionsItemSelected(item);
    }


    // toa do vi tri hien tai
    private void getDeviceLocation() {
        m_fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        try {
            if (m_locationPermissionsGranted) {
                final Task location = m_fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            m_current = (Location) task.getResult();
                            moveCamera(new LatLng(m_current.getLatitude(), m_current.getLongitude()), DEFAULT_ZOOM);
                            updateMarker(m_current.getLatitude(), m_current.getLongitude());
                            TextView departure = (TextView) findViewById(R.id.departureText);
                            departure.setText(MapHandleUtils.getAddressFromLocation(getApplicationContext(), m_current));
                            handleIntentAction();
                        } else {
                            Toast.makeText(MapActivity.this, "Khong xac dinh duoc", Toast.LENGTH_SHORT).show();
                            TextToSpeechUtils.speak(MapActivity.this, "Không thể xác định vị trí hiện tại.");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Toast.makeText(MapActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // chinh camera
    private void moveCamera(LatLng latLng, float zoom) {
        m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //kiem tra quyen de ho tro cho viec lay vi tri hien tai
    private void checkLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            m_locationPermissionsGranted = true;
        } else {
            ActivityCompat.requestPermissions(MapActivity.this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // TODO enable my Location crash app
    private void updateMapUI() throws SecurityException {
        if (m_map == null)
            return;
        if (m_locationPermissionsGranted) {
            getDeviceLocation();
            setMyLocation();
//            m_map.setMyLocationEnabled(true);
//            m_map.setOnMyLocationClickListener((GoogleMap.OnMyLocationClickListener) this);
//            m_map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
//            m_map.setMyLocationEnabled(false);
//            m_map.getUiSettings().setMyLocationButtonEnabled(false);
            checkLocationPermission();
        }
        m_map.getUiSettings().setZoomControlsEnabled(true);
        m_map.getUiSettings().setCompassEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        m_map = googleMap;

        this.checkLocationPermission();
        try {
            this.updateMapUI();
        } catch (SecurityException ex) {
            Toast.makeText(this.getApplicationContext(), "Lack of permission", Toast.LENGTH_SHORT).show();
            TextToSpeechUtils.speak(MapActivity.this, "Ứng dụng không thể dùng bản đồ");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @NonNull int[] grantResults) {
        m_locationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
//                    for (int i = 0; i < grantResults.length; i++) {
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            m_locationPermissionsGranted = false;
//                            return;
//                        }
//                    }
                    m_locationPermissionsGranted = true;
                }
                break;
            }
            case MainActivity.CALL_REQ_CODE:
                TextToSpeechUtils.speak(this, "Quyền đã được cấp, vui lòng thực hiện lại cuộc gọi.");
                break;
            case MainActivity.SEND_SMS_REQ_CODE:
                TextToSpeechUtils.speak(this, "Quyền đã được cấp, vui lòng gửi lại tin nhắn.");
                break;
        }
    }

    @Override
    public void onDirectionFinderStart() {
//        Toast.makeText(MapActivity.this, "onDirectionFinderStart", Toast.LENGTH_SHORT).show();
        if (m_originMarkers.size() != 0) {
//            Toast.makeText(MapActivity.this, "" + m_originMarkers.size(), Toast.LENGTH_SHORT).show();
            for (Marker m_marker : m_originMarkers) {
                Toast.makeText(MapActivity.this, m_marker.getTitle() + "", Toast.LENGTH_SHORT).show();
                m_marker.remove();
            }
        }

        if (m_destinationMarkers != null) {
            for (Marker m_marker : m_destinationMarkers) {
                m_marker.remove();
            }
        }

        if (m_polylinePaths != null) {
            for (Polyline polyline : m_polylinePaths) {
                polyline.remove();
            }
        }
    }

    /**
     * Handle direction result from DirectionFinder class
     * In this scope, there is one route in result
     *
     * @param routes
     */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        TextToSpeechUtils.speak(MapActivity.this, TextToSpeechUtils.TEXT_REQ_DIRECTION_DONE);
        m_isStartToGo = 0;
        m_polylinePaths = new ArrayList<>();
        m_originMarkers = new ArrayList<>();
        m_destinationMarkers = new ArrayList<>();
        m_directedRoute.addAll(routes);
        TextView instructionTextView = (TextView) findViewById(R.id.instructionText);
        instructionTextView.append(routes.size() + " routes\n");
        if (routes.size() == 0) {
            TextToSpeechUtils.speak(this, TextToSpeechUtils.TEXT_REQ_DIRECTION_NO_RESULT);
            return;
        }

        for (Route route : routes) {
            moveCamera(route.getStartLocation(), DEFAULT_ZOOM);
            m_originMarkers.add(m_map.addMarker(new MarkerOptions()
                    .title(route.getStartAddress())
                    .position(route.getStartLocation())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .snippet("Start: Điểm Khởi Hành")
            ));

            m_destinationMarkers.add(m_map.addMarker(new MarkerOptions()
                    .title(route.getEndAddress())
                    .snippet("End: Điểm Đến")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .position(route.getEndLocation())));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            List<LatLng> points = route.getPolylines();
            for (int i = 0; i < points.size(); i++)
                polylineOptions.add(points.get(i));

            m_polylinePaths.add(m_map.addPolyline(polylineOptions));
            instructionTextView.append(MapHandleUtils.optimizeInstruction(route.getHTMLInstruction()));
        }
        guide(m_current.getLatitude(), m_current.getLongitude());
    }

    /**
     * guiding user by voice. Note that there is one route only
     *
     * @param latitude
     * @param longitude
     */
    protected void guide(double latitude, double longitude) {
        if (m_directedRoute == null || m_directedRoute.size() == 0)
            return;
        if (m_isStartToGo == 1)
            guideFromSecondStep(latitude, longitude);
        else
            guideFirstTime(latitude, longitude);
    }

    private void guideFromSecondStep(double latitude, double longitude) {
        Route route = m_directedRoute.get(0);
        try {
            Step temp = route.getClosestStepByLocation(latitude, longitude, THRESHOLD_TO_POINT);
            if (temp != null && this.m_curretStep == null)
                TextToSpeechUtils.speak(MapActivity.this, temp.getSimpleInstruction());
            else if (temp != null && !this.m_curretStep.equals(temp)) {
                this.m_curretStep = temp;
                TextToSpeechUtils.speak(MapActivity.this, temp.getSimpleInstruction());
            }

            this.guideArrived(route, temp);
        } catch (Exception ex) {
//            Intent intent = new Intent(this, MapActivity.class);
//            intent.setAction("map_exception");
//            intent.putExtra("trace", ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
//            startActivity(intent);
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void guideFirstTime(double latitude, double longitude) {
        Route route = m_directedRoute.get(0);
        double distanceToStart = MapHandleUtils.calculateDistanceBetweenPointsFromLatLng(route.getStartLocation().latitude,
                route.getStartLocation().longitude, latitude, longitude);
        // notice far from departure in the first time
        if (distanceToStart > THRESHOLD_TO_POINT && m_isStartToGo == 0) {
            TextToSpeechUtils.speak(this, TextToSpeechUtils.TEXT_REQ_DIRECTION_DONE_FAR_FROM_START);
            m_isStartToGo = -1;
            return;
        }

        if (distanceToStart > THRESHOLD_TO_POINT) {
            return;
        }

        String instructDirection = route.getStartStep().getSimpleInstruction();
        if (!instructDirection.contains(m_compassSensorListener.getOrientationInString())) {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
        } else {
            m_isStartToGo = 1;
            TextToSpeechUtils.speak(this, TextToSpeechUtils.TEXT_GO_AHEAD);
        }

        this.guideArrived(route, route.getStartStep());
    }

    private boolean isUserNearDestination() {
        return (MapHandleUtils.calculateDistanceBetweenPointsFromLatLng(m_directedRoute.get(0).getEndLocation(), new LatLng(this.m_current.getLatitude(), this.m_current.getLongitude())) <= THRESHOLD_TO_POINT) ||
                (MapHandleUtils.calculateDistanceBetweenPointsFromLatLng(m_des, new LatLng(this.m_current.getLatitude(), this.m_current.getLongitude())) <= THRESHOLD_TO_POINT);
    }

    private void guideArrived(Route route, Step step) {
        if (!this.isUserNearDestination())
            return;

        Toast.makeText(this, "Near destination", Toast.LENGTH_SHORT).show();

        if (route.isLastStep(step)) {
            TextToSpeechUtils.speak(this, TextToSpeechUtils.TEXT_ARRIVED);
            while (TextToSpeechUtils.isSpeaking()) {
            }
            this.cancelDirection(null);
        }
    }

    public void cancelDirection(View view) {
        if (m_directedRoute == null || m_map == null)
            return;
        this.m_directedRoute = null;
        m_isStartToGo = 0;
        this.m_map.clear();
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void directToGoBack(View view) {
        if (TEMP_LOCATION == null) {
            TextToSpeechUtils.speak(this, TextToSpeechUtils.TEXT_REQ_DIRECTION_MISSING);
            return;
        }
        m_des = new LatLng(TEMP_LOCATION.getLatitude(), TEMP_LOCATION.getLongitude());
        TextToSpeechUtils.speak(this, TextToSpeechUtils.TEXT_REQ_DIRECTION_COMEBACK);
        this.sendRequestGoFromHereToPlace(m_des.latitude, m_des.longitude);
    }
}
