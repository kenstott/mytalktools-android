package com.MTA.MyTalkMobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

@Keep
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int PLACE_PICKER_REQUEST = 1;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mLastLocation;
    private View mainView;
    private Circle circle;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        String externalUrl = bundle.getString("externalUrl");
        if (externalUrl != null && externalUrl.startsWith("mtgeo:/")) {
            String[] parts = externalUrl.replace("mtgeo:/", "").split(",");
            double latitude = Float.parseFloat(parts[0]);
            double longitude = Float.parseFloat(parts[1]);
            mLastLocation = new LatLng(latitude, longitude);
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mainView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .build();
            }
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapmenu, menu);
        if (mLastLocation != null) {
            MenuItem mi = menu.findItem(R.id.AddLocation);
            mi.setTitle("Edit Location");
            mi.setIcon(R.drawable.ic_menu_edit);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public final boolean onOptionsItemSelected(final MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.ExitMap) {
            setResult(0, null);
            finish();
            return false;
        } else if (itemId == R.id.AddLocation) {
            Intent data = new Intent();
            LatLng center = mMap.getCameraPosition().target;
            data.putExtra("Latitude", center.latitude);
            data.putExtra("Longitude", center.longitude);
            setResult(1, data);
            finish();
            return false;
        } else if (itemId == R.id.FindPlace) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException |
                     GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    private LatLng offsetLatLng(LatLng ll, double dn, double de) {
        //Earth’s radius, sphere
        double R = 6378137;

        //Coordinate offsets in radians
        double dLat = dn / R;
        double dLon = (de / (R * Math.cos(Math.PI * ll.latitude / 180.0)));

        //OffsetPosition, decimal degrees
        double latO = ll.latitude + dLat * 180.0 / Math.PI;
        double lonO = ll.longitude + dLon * 180.0 / Math.PI;
        return new LatLng(latO, lonO);
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            //final AutocompleteFilter filter = AutocompleteFilter.create(null);
            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(37.4, -122.1))
                    .radius(50); // In meters
            mMap.setOnCameraMoveListener(() -> circle.setCenter(mMap.getCameraPosition().target));

// Get back the mutable Circle
            circle = mMap.addCircle(circleOptions);
            if (mLastLocation != null) {
                mainView.postDelayed(() -> {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mLastLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                }, 1000);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLastLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (location != null) {
                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mLastLocation));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }
        }
    }
}
