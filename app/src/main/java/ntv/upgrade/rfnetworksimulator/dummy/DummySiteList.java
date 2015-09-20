package ntv.upgrade.rfnetworksimulator.dummy;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ntv.upgrade.rfnetworksimulator.site.Site;

/**
 * Created by Paulino on 9/20/2015.
 */
public class DummySiteList {
    // Sites List
    public static ArrayList<Site> mSitesArrayList = new ArrayList<>();

    public DummySiteList() {
        createSitesList();
    }

    /**
     * List of sites to be added to the database,
     * works as initial data for testing porpuses.
     */
    private void createSitesList() {

        int i = 1;
        mSitesArrayList.add(new Site(i++,
                "30 de Marzo", new LatLng(18.474259, -69.895146), 39.6240, true, 345, 1.0, 160, 5.0, 240, 2.0));
        mSitesArrayList.add(new Site(i++,
                "ARS Palic", new LatLng(18.47458, -69.9162), 33.5280, true, 350, 3.5, 110, 3.5, 220, 3.5));
        mSitesArrayList.add(new Site(i++,
                "Gazcue", new LatLng(18.468358, -69.90468), 30.4800, false, 0, 2.0, 120, 5.0, 225, 4.0));
        mSitesArrayList.add(new Site(i++,
                "Gazcue2", new LatLng(18.4597, -69.9125), 16.7640, true, 0, 3.0, 120, 3.0, 240, 3.0));
        mSitesArrayList.add(new Site(i++,
                "Melia", new LatLng(18.463427, -69.898148), 42.6379, true, 340, 0.0, 50, 1.0, 260, 1.0));
        mSitesArrayList.add(new Site(i++,
                "UNIBE", new LatLng(18.475055, -69.9094), 42.6720, true, 40, 3.0, 150, 4.0, 270, 4.0));
        mSitesArrayList.add(new Site(i++,
                "UTESA", new LatLng(18.466756, -69.91167), 42.6720, true, 60, 2.0, 140, 3.0, 270, 1.0));
        mSitesArrayList.add(new Site(i++,
                "Villa Francisca", new LatLng(18.480037, -69.88862), 30.4800, false, 15, 4.0, 120, 4.0, 240, 4.0));
        mSitesArrayList.add(new Site(i++,
                "Villa-Juana", new LatLng(18.4831, -69.9056), 39.6240, false, 0, 9.0, 120, 9.0, 240, 9.0));
        mSitesArrayList.add(new Site(i++,
                "Zona Colonial", new LatLng(18.475539, -69.884344), 21.3400, true, 340, 7.0, 150, 6.0, 270, 7.0));
    }

}
