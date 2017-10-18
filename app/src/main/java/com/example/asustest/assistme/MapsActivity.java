package com.example.asustest.assistme;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mapscomp.GetNearbyPlacesData;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    //int PROXIMITY_RADIUS = 2000;
    public static double latitude, longitude;
    double end_latitude, end_longitude;
    PlaceAutocompleteFragment autocompleteFragment;
    LatLng destLatLng, trainLatLng;
    public static final int REQUEST_LOCATION_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }

        // Autocomplete fragment
        autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                MarkerOptions mo = new MarkerOptions();
                LatLng latLng = place.getLatLng();
                mo.position(latLng);
                mo.title("Search Results");
                mMap.addMarker(mo);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); //Remove if multiple results
                Log.i("Destination", "Place: "+place.getName());

                /*
                Object dataTransfer = new Object[3];
                url = getDirectionsUrl(latitude, longitude, end_latitude, end_longitude);
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude,end_longitude);

                getDirectionsData.execute(dataTransfer);
                 */

            }

            @Override
            public void onError(Status status) {

                Log.i("Destination", "An error occured: "+status);
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If permission is granted
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    //If permission is denied
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
                }
                return;
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //Remove the IF and ELSE

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if(currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, (com.google.android.gms.location.LocationListener) this);
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, (com.google.android.gms.location.LocationListener) this); //Sure cast
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void goSearch(View view) {
        switch (view.getId())
        {
            /*case R.id.B_search:
                {
//                EditText tf_location = (EditText) findViewById(R.id.TF_location);
//                String location = tf_location.getText().toString();

//                List<Address> addressList = null;
                MarkerOptions mo = new MarkerOptions();

//                if (!location.equals("")){
//                    Geocoder geocoder = new Geocoder(this);
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 5);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    //Check size of addressList
//                    for (int i=0; i<addressList.size(); i++){
//                        Address myAddress = addressList.get(i);
//                        LatLng latLng = new LatLng(myAddress.getLatitude(),myAddress.getLongitude());
//                        mo.position(latLng);
//                        mo.title("Search Results");
//                        mMap.addMarker(mo);
//                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); //Remove if multiple results
//                    }
//                }

            }
            break;*/

            case R.id.B_station:
                mMap.clear();
                String station = "train_station";
                String url = getUrl(latitude, longitude, station);
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                getNearbyPlacesData.execute(dataTransfer);
                trainLatLng = getNearbyPlacesData.getLatLng(); //GETS THE LOCATION OF THE TRAIN STATION
                Toast.makeText(MapsActivity.this, "Showing Nearby Train Stations", Toast.LENGTH_LONG).show();
                break;

            case R.id.B_to:
                dataTransfer = new Object[3];
                url = getDirectionsUrl(latitude, longitude, end_latitude, end_longitude);
                //GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude,end_longitude);

                //getDirectionsData.execute(dataTransfer);

                /*//distance between two places
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(end_latitude, end_longitude));
                markerOptions.title("Destination");
                markerOptions.draggable(true);

                float results[] = new float[10];
                Location.distanceBetween(latitude, longitude, end_latitude, end_longitude, results);
                markerOptions.snippet("Distance = "+results[0]);
                mMap.addMarker(markerOptions);*/
                break;

        }
    }

    private String getDirectionsUrl(double A_lat, double A_lng, double B_lat, double B_lng){
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+A_lat+","+A_lng);
        googleDirectionsUrl.append("&destination="+B_lat+","+B_lng);
        googleDirectionsUrl.append("&key="+"AIzaSyCeko4AxuvPO6rJULEUH5ee4HfDyLSK0qU");

        return googleDirectionsUrl.toString();
    }

    /*

    private String getDirectionsUrl(){
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyCeko4AxuvPO6rJULEUH5ee4HfDyLSK0qU");

        return googleDirectionsUrl.toString();
    }

     */

    private String getUrl(double latitude, double longitude, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyAib7lBWc7HtYdmd709qHm_Cux1E5P-XvE
        googlePlaceUrl.append("location="+latitude+","+longitude);
        //googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&rankby=distance");
        googlePlaceUrl.append("&type="+nearbyPlace);
        //googlePlaceUrl.append("&keyword=railway");
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyAib7lBWc7HtYdmd709qHm_Cux1E5P-XvE");

        return googlePlaceUrl.toString();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;
    }
}
