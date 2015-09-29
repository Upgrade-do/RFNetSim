package ntv.upgrade.rfnetworksimulator;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ntv.upgrade.rfnetworksimulator.data.JsonReader;
import ntv.upgrade.rfnetworksimulator.data.JsonWriter;
import ntv.upgrade.rfnetworksimulator.site.Site;
import ntv.upgrade.rfnetworksimulator.tools.CheckServices;
import ntv.upgrade.rfnetworksimulator.tools.Preferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        EditSiteDialogFragment.OnFragmentInteractionListener,
        AreYouSureDialogFragment.OnFragmentInteractionListener {

    // List of sites
    protected static ArrayList<Site> mSitesArrayList = new ArrayList<>();
    // Site used to handle a single site
    protected static Site tempSite = null;
    protected static LatLng tempGeolocation;
    // for log pospuses
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    // name of the file to preserve data
    private final String SITES_DATA_FILE_NAME = "sites_data";
    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;

    // Google map object.
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        try {
            InputStream in = openFileInput(SITES_DATA_FILE_NAME);
            JsonReader reader = new JsonReader();
            mSitesArrayList = reader.readJsonStream(in);
            displayChanges();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        try {
            OutputStream out = openFileOutput(SITES_DATA_FILE_NAME, Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter();
            writer.writeJsonStream(out, mSitesArrayList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating action button to create a new site at the center of the map when clicked
        FloatingActionButton fab_add_new_site = (FloatingActionButton) findViewById(R.id.fab_add_new_site);
        fab_add_new_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempGeolocation = mCameraPosition.target;
                confirmNewSiteCreationRequest("create");
            }
        });

        // Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (CheckServices.checkPlayServices(this)) {
            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
        } else finish();

        if (!CheckServices.isGoogleMapsInstalled(this)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Install Google Maps");
            builder.setCancelable(false);
            builder.setPositiveButton("Install", getGoogleMapsListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * Navigates to Google play to download google maps
     */
    public DialogInterface.OnClickListener getGoogleMapsListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);
                //Finish the activity so they can't circumvent the check
                finish();
            }
        };
    }

    /**
     * Sets up the map if it not already instantiated.
     * If it isn't installed will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMapType(Integer.parseInt(
                        Preferences.getPreferredMapStyle(this)));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mCameraPosition = cameraPosition;
            }
        });

        setInfoWindows();
        setOnMapClickListener();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_map:
                break;
            case R.id.nav_sites_list:
                startActivity(new Intent(this, SitesListActivity.class));
                break;
            case R.id.nav_add_site:
                tempGeolocation = mCameraPosition.target;
                confirmNewSiteCreationRequest("create");
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            default:
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Builds Google Api Client to {@link #mGoogleApiClient).
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /**
     * This method is called on a successful connection.
     * Retrieves last known location of the device and calls
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
            centerMapOnLocation(mLastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Snackbar.make(this.getCurrentFocus(), "Doh, could not connect to Google Api Client", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGoogleApiClient.connect();
                    }
                })
                        //.setActionTextColor(R.color.tutorial_edit_site)
                .show();
    }

    /**
     * Center map on a given @param location
     */
    private void centerMapOnLocation(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    private void setInfoWindows() {

        tempSite = null;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            public View getInfoWindow(Marker arg0) {

                View view = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                TextView siteName = (TextView) view.findViewById(R.id.siteName_editText);
                TextView siteCoordinates = (TextView) view.findViewById(R.id.siteCoordinates_textView);
                TextView siteHeight = (TextView) view.findViewById(R.id.siteHeight_textView);

                TextView alphaAzimuth = (TextView) view.findViewById(R.id.alphaAzimuth_textView);
                TextView betaAzimuth = (TextView) view.findViewById(R.id.betaAzimuth_textView);
                TextView gammaAzimuth = (TextView) view.findViewById(R.id.gammaAzimuth_textView);

                TextView alphaTilt = (TextView) view.findViewById(R.id.alphaTilt_textView);
                TextView betaTilt = (TextView) view.findViewById(R.id.betaTilt_textView);
                TextView gammaTilt = (TextView) view.findViewById(R.id.gammaTilt_textView);

                tempSite = getSiteByName(arg0.getTitle());

                if (tempSite != null) {

                    siteName.setText(tempSite.getName());
                    siteCoordinates.setText(String.format("( %.6f, %.6f )"
                            , tempSite.getGeo().latitude, tempSite.getGeo().longitude));
                    siteHeight.setText(String.format("%.2f mts", tempSite.getHeight()));

                    alphaAzimuth.setText(String.format("%d", tempSite.getAlpha().getAzimuth()));
                    betaAzimuth.setText(String.format("%d", tempSite.getBeta().getAzimuth()));
                    gammaAzimuth.setText(String.format("%d", tempSite.getGamma().getAzimuth()));

                    alphaTilt.setText(String.format("%.1f", tempSite.getAlpha().getTilt()));
                    betaTilt.setText(String.format("%.1f", tempSite.getBeta().getTilt()));
                    gammaTilt.setText(String.format("%.1f", tempSite.getGamma().getTilt()));

                    if (tempSite.isOperational()) {
                        siteName.setBackgroundResource(R.color.colorGreen);
                    } else siteName.setBackgroundResource(R.color.colorRed);
                }
                return view;
            }
            public View getInfoContents(Marker arg0) {
                return null;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                openEditSiteDialogFragment("edit");
            }
        });
    }

    private void setOnMapClickListener() {

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng geo) {

                tempSite = getSiteByLocation(geo);

                if (tempSite != null) {
                    confirmNewSiteCreationRequest("delete");
                } else {
                    tempGeolocation = geo;
                    confirmNewSiteCreationRequest("create");
                }
            }
        });

    }

    /**
     * This method is triggered after user clicks on Yes button from the add/delete dialog
     * box, receives a @param action which contains a string with the action to be performed.
     */
    @Override
    public void onAcceptButtonClicked(String action) {

        switch (action) {
            case "create":
                // creates a new blank site to be edited and added to list.
                tempSite = null;
                openEditSiteDialogFragment(action);
                break;

            case "delete":
                // deletes selected site after confirmation.
                deleteSite(tempSite);
                displayChanges();
                break;
        }
    }

    /**
     * This method is triggered after user clicks on Save button from the create/edit dialog
     * fragment, receives a @param action which contains a string with the action to be performed.
     */
    @Override
    public void onSaveButtonClicked(String action) {

        switch (action) {

            case "create":
                if (getSiteByLocation(tempSite.getGeo()) == null) {
                    mSitesArrayList.add(tempSite);
                } else {
                    Toast.makeText(this,
                            String.format(
                                    "Ups!... A site already exists at coordinates: (%.4f, %.4f)",
                                    tempSite.getGeo().latitude, tempSite.getGeo().longitude),
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;

            case "edit":
                mSitesArrayList.add(tempSite);
                break;
        }
        tempSite = null;
        tempGeolocation = null;
        displayChanges();
    }

    //Custom Methods
    //----------------------------------------------------------------------------------------------

    private void confirmNewSiteCreationRequest(String action) {
        DialogFragment newAreYouSureDialogFragment;
        newAreYouSureDialogFragment = AreYouSureDialogFragment.newInstance(action);
        newAreYouSureDialogFragment.show(getFragmentManager(), "Are You Sure DialogFragment");
    }

    private void openEditSiteDialogFragment(String action) {
        DialogFragment newEditSiteDialogFragment;
        newEditSiteDialogFragment = EditSiteDialogFragment.newInstance(action);
        newEditSiteDialogFragment.show(getFragmentManager(), "Edit DialogFragment");
    }

    private Site getSiteByName(String siteName) {

        for (Site site : mSitesArrayList) {
            if (site.getName().equals(siteName)) {
                return site;
            }
        }
        return null;
    }

    private Site getSiteByLocation(LatLng geo) {

        for (Site site : mSitesArrayList) {
            if (Math.abs(site.getGeo().latitude - geo.latitude) < 0.001
                    && Math.abs(site.getGeo().longitude - geo.longitude) < 0.001) {
                return site;
            }
        }
        return null;
    }

    private boolean deleteSite(Site tempSite) {

        return mSitesArrayList.remove(tempSite);
    }

    private void drawSiteList() {
        for (Site site : mSitesArrayList) {
            drawSite(site);
        }
    }

    /**
     * Draws a single site includding all tree sectors
     */
    private void drawSite(Site site) {

        BitmapDescriptor image;

        if (site.isOperational()) {
            image = BitmapDescriptorFactory.fromResource(R.mipmap.antenna_on);

            mMap.addPolygon(new PolygonOptions()
                    .add(site.getGeo(), site.getAlpha().getP1(), site.getAlpha().getP2())
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(1)
                    .fillColor(Color.argb(90, 255, 255, 0)));

            mMap.addPolygon(new PolygonOptions()
                    .add(site.getGeo(), site.getBeta().getP1(), site.getBeta().getP2())
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(1)
                    .fillColor(Color.argb(90, 255, 255, 0)));

            mMap.addPolygon(new PolygonOptions()
                    .add(site.getGeo(), site.getGamma().getP1(), site.getGamma().getP2())
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(1)
                    .fillColor(Color.argb(90, 255, 255, 0)));
        } else {
            image = BitmapDescriptorFactory.fromResource(R.mipmap.antenna_off);
        }

        mMap.addMarker(new MarkerOptions()
                .position(site.getGeo())
                .title(site.getName())
                .draggable(false)
                .anchor(0.5f, 0.5f)
                .icon(image));
    }

    private void displayChanges() {
        mMap.clear();
        drawSiteList();
    }
}
