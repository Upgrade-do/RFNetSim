package ntv.upgrade.rfnetworksimulator;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import com.google.android.gms.location.LocationRequest;
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

import java.util.ArrayList;

import ntv.upgrade.rfnetworksimulator.site.Site;
import ntv.upgrade.rfnetworksimulator.tools.CheckServices;
import ntv.upgrade.rfnetworksimulator.tools.Preferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        EditSiteDialogFragment.OnFragmentInteractionListener,
        AreYouSureDialogFragment.OnFragmentInteractionListener {

    protected static ArrayList<Site> mSitesArrayList;

    // Site used to handle a single site
    protected static Site mSite;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    Location mLastLocation;
    // Client used to interact with Google APIs.
    private GoogleApiClient mGoogleApiClient;
    // Might be null if Google Play services APK is not available.
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    // Monitors camera position
    private CameraPosition mCameraPosition;
    // LatLng
    private LatLng mLatLng;
    // Progress dialog for the json request
    private ProgressDialog progressDialog;

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
        if (mSitesArrayList == null)
            mSitesArrayList = createDummySitesList();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab_add_new_site = (FloatingActionButton) findViewById(R.id.fab);
        fab_add_new_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newAreYouSureDialogFragment;
                newAreYouSureDialogFragment = AreYouSureDialogFragment.newInstance("create");
                newAreYouSureDialogFragment.show(getFragmentManager(), "AreYouSure DialogFragment");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (CheckServices.checkPlayServices(this)) {
            if (mGoogleApiClient == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading map and sites list. Please wait...");
                progressDialog.setCancelable(false);
                buildGoogleApiClient();
            }
        } else finish();

    }

    /**
     * Sets up the map if it not already instantiated.
     * If it isn't installed will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            if (isGoogleMapsInstalled()) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map))
                        .getMap();
                if (mMap != null) {
                    setUpMap();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Install Google Maps");
                builder.setCancelable(false);
                builder.setPositiveButton("Install", getGoogleMapsListener());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    /**
     * Checks whether google maps is installed and updated on the device
     *
     * @return true if available
     */
    public boolean isGoogleMapsInstalled() {
        try {
            getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Navigates to Google play to download google maps
     *
     * @return
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMapType(
                Integer.parseInt(
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
                startActivity(
                        new Intent(this, SettingsActivity.class));
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
                startActivity(
                        new Intent(this, SitesListActivity.class));
                break;
            case R.id.nav_add_site:
                DialogFragment newAreYouSureDialogFragment;
                newAreYouSureDialogFragment = AreYouSureDialogFragment.newInstance("create");
                newAreYouSureDialogFragment.show(getFragmentManager(), "AreYouSure DialogFragment");
                ;
                break;
            case R.id.nav_settings:
                startActivity(
                        new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_about:
                startActivity(
                        new Intent(this, AboutActivity.class));
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

        showProgressDialog();

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
        mLastLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) centerMapOnLocation(mLastLocation);
        hideProgressDialog();
        displayChanges();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),
                "Could not connect to server",
                Toast.LENGTH_LONG).show();
        hideProgressDialog();
    }

    /**
     * Center map on a given @param location
     */
    private void centerMapOnLocation(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    private void centerMapOnLastCamera() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                mCameraPosition.target, mCameraPosition.zoom));
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void setInfoWindows() {

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            public View getInfoWindow(Marker arg0) {

                View view = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                TextView siteName = (TextView) view.findViewById(R.id.siteName_editText);
                TextView siteCoordinates = (TextView) view.findViewById(R.id.siteCoordinates_textView);
                TextView siteHeight = (TextView) view.findViewById(R.id.siteHeight_textView);
                TextView alphaAzimuth = (TextView) view.findViewById(R.id.alphaAzimuth_textView);
                TextView alphaTilt = (TextView) view.findViewById(R.id.alphaTilt_textView);
                TextView betaAzimuth = (TextView) view.findViewById(R.id.betaAzimuth_textView);
                TextView betaTilt = (TextView) view.findViewById(R.id.betaTilt_textView);
                TextView gammaAzimuth = (TextView) view.findViewById(R.id.gammaAzimuth_textView);
                TextView gammaTilt = (TextView) view.findViewById(R.id.gammaTilt_textView);

                for (Site site : mSitesArrayList) {
                    if (site.getName().equals(arg0.getTitle())) {

                        siteName.setText(site.getName());
                        siteCoordinates.setText("( " + site.getPosition().latitude
                                + ", " + site.getPosition().longitude + " )");

                        siteHeight.setText(site.getHeight() * 1000 + " mts");

                        alphaAzimuth.setText(String.format("%d", site.getAlpha().getAzimuth()));
                        betaAzimuth.setText(String.format("%d", site.getBeta().getAzimuth()));
                        gammaAzimuth.setText(String.format("%d", site.getGamma().getAzimuth()));

                        alphaTilt.setText(String.format("%.1f", site.getAlpha().getTilt()));
                        betaTilt.setText(String.format("%.1f", site.getBeta().getTilt()));
                        gammaTilt.setText(String.format("%.1f", site.getGamma().getTilt()));

                        if (site.getStatus()) {
                            siteName.setBackgroundResource(R.color.tutorial_edit_site);
                        } else siteName.setBackgroundResource(R.color.tutorial_delete_site);

                        break;
                    }
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

                for (Site site : mSitesArrayList) {
                    if (site.getName().equals(marker.getTitle())) {

                        mSite = site;

                        DialogFragment newDialogFragment = EditSiteDialogFragment
                                .newInstance(site.getPosition().latitude, site.getPosition().longitude, "edit");
                        newDialogFragment.show(getFragmentManager(), "Edit DialogFragment");
                        break;
                    }
                }
            }
        });
    }

    private void setOnMapClickListener() {

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                boolean siteFound = false;
                DialogFragment newAreYouSureDialogFragment = null;

                for (Site site : mSitesArrayList) {
                    if (Math.abs(site.getPosition().latitude - latLng.latitude) < 0.001
                            && Math.abs(site.getPosition().longitude - latLng.longitude) < 0.001) {
                        siteFound = true;

                        mSite = site;
                        newAreYouSureDialogFragment = AreYouSureDialogFragment.newInstance("delete");
                        break;
                    }
                }

                if (!siteFound) {

                    newAreYouSureDialogFragment = AreYouSureDialogFragment.newInstance("create");
                    mLatLng = new LatLng(latLng.latitude, latLng.longitude);
                }
                newAreYouSureDialogFragment.show(getFragmentManager(), "AreYouSure DialogFragment");
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
                mSite = new Site();
                DialogFragment newDialogFragment;
                if (mLatLng == null) {
                    newDialogFragment = EditSiteDialogFragment
                            .newInstance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), action);
                } else {
                    // opens dialog fragment for user interface
                    newDialogFragment = EditSiteDialogFragment
                            .newInstance(mLatLng.latitude, mLatLng.longitude, action);
                }
                newDialogFragment.show(getFragmentManager(), "Create DialogFragment");
                break;

            case "delete":
                // deletes selected site after confirmation.
                deleteSite(mSite.getName());
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
                boolean siteFound = false;

                for (Site site : mSitesArrayList) {
                    if (Math.abs(site.getPosition().latitude - mSite.getPosition().latitude) < 0.01
                            && Math.abs(site.getPosition().longitude - mSite.getPosition().longitude) < 0.01) {
                        siteFound = true;

                        Toast.makeText(this,
                                String.format(
                                        "Ups!... A site already exists at coordinates: (%.4f, %.4f)",
                                        mSite.getPosition().latitude, mSite.getPosition().longitude),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
                if (!siteFound) {
                    mSitesArrayList.add(mSite);
                }
                mSite = null;
                break;

            case "edit":
                break;
        }
        displayChanges();
    }

    //Custom Methods
    //----------------------------------------------------------------------------------------------

    public void drawSiteList() {
        for (Site site : mSitesArrayList) {
            drawSite(site);
        }
    }

    /**
     * Draws a single site includding all tree sectors
     */
    public void drawSite(Site site) {

        BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.mipmap.antenna_off);

        if (site.getStatus()) {
            image = BitmapDescriptorFactory.fromResource(R.mipmap.antenna_on);

            mMap.addPolygon(new PolygonOptions()
                    .add(site.getPosition(), site.getAlpha().getP1(), site.getAlpha().getP2())
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(1)
                    .fillColor(Color.argb(90, 255, 255, 0)));

            mMap.addPolygon(new PolygonOptions()
                    .add(site.getPosition(), site.getBeta().getP1(), site.getBeta().getP2())
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(1)
                    .fillColor(Color.argb(90, 255, 255, 0)));

            mMap.addPolygon(new PolygonOptions()
                    .add(site.getPosition(), site.getGamma().getP1(), site.getGamma().getP2())
                    .strokeColor(Color.YELLOW)
                    .strokeWidth(1)
                    .fillColor(Color.argb(90, 255, 255, 0)));
        }

        mMap.addMarker(new MarkerOptions()
                .position(site.getPosition())
                .title(site.getName())
                .snippet(site.toString())
                .draggable(false)
                .anchor(0.5f, 0.5f)
                .icon(image));
    }

    public void deleteSite(String siteName) {

        for (Site site : mSitesArrayList) {
            if (site.getName().equals(siteName)) {
                mSitesArrayList.remove(site);
                break;
            }
        }
        displayChanges();
    }

    public void displayChanges() {
        mMap.clear();
        drawSiteList();
    }


    private ArrayList<Site> createDummySitesList() {

        ArrayList<Site> siteList = new ArrayList<>();

        if (Preferences.getPreferenceDummyData(this)) {
            int i = 1;
            siteList.add(new Site(i++,
                    "30 de Marzo", new LatLng(18.474259, -69.895146), 39.6240, true, 345, 1.0, 160, 5.0, 240, 2.0));
            siteList.add(new Site(i++,
                    "ARS Palic", new LatLng(18.47458, -69.9162), 33.5280, true, 350, 3.5, 110, 3.5, 220, 3.5));
            siteList.add(new Site(i++,
                    "Gazcue", new LatLng(18.468358, -69.90468), 30.4800, false, 0, 2.0, 120, 5.0, 225, 4.0));
            siteList.add(new Site(i++,
                    "Gazcue2", new LatLng(18.4597, -69.9125), 16.7640, true, 0, 3.0, 120, 3.0, 240, 3.0));
            siteList.add(new Site(i++,
                    "Melia", new LatLng(18.463427, -69.898148), 42.6379, true, 340, 0.0, 50, 1.0, 260, 1.0));
            siteList.add(new Site(i++,
                    "UNIBE", new LatLng(18.475055, -69.9094), 42.6720, true, 40, 3.0, 150, 4.0, 270, 4.0));
            siteList.add(new Site(i++,
                    "UTESA", new LatLng(18.466756, -69.91167), 42.6720, true, 60, 2.0, 140, 3.0, 270, 1.0));
            siteList.add(new Site(i++,
                    "Villa Francisca", new LatLng(18.480037, -69.88862), 30.4800, false, 15, 4.0, 120, 4.0, 240, 4.0));
            siteList.add(new Site(i++,
                    "Villa-Juana", new LatLng(18.4831, -69.9056), 39.6240, false, 0, 9.0, 120, 9.0, 240, 9.0));
            siteList.add(new Site(i++,
                    "Zona Colonial", new LatLng(18.475539, -69.884344), 21.3400, true, 340, 7.0, 150, 6.0, 270, 7.0));
        }
        return siteList;
    }
}
