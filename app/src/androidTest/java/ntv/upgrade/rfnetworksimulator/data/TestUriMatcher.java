package ntv.upgrade.rfnetworksimulator.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Paulino on 9/13/2015.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String SITE_NAME_QUERY = "30 de Marzo";

    // content://ntv.upgrade.rfnetworksimulator/site"
    private static final Uri TEST_SITE_DIR = SitesContract.SiteEntry.CONTENT_URI;
    private static final Uri TEST_SITE_WITH_SITE_NAME= SitesContract.SiteEntry.buildSiteWithSiteName(SITE_NAME_QUERY);
    // content://ntv.upgrade.rfnetworksimulator/sector"
    private static final Uri TEST_SECTOR_DIR = SitesContract.SectorEntry.CONTENT_URI;

    /**
     * tests that your UriMatcher returns the correct integer value
     * for each of the Uri types that our ContentProvider can handle
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = SitesProvider.buildUriMatcher();

        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                testMatcher.match(TEST_SITE_DIR), SitesProvider.SITES);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_SITE_WITH_SITE_NAME), SitesProvider.SITES_WITH_SITE_NAME);
        assertEquals("Error: The LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_SECTOR_DIR), SitesProvider.SECTOR);
    }

}
