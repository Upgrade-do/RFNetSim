package ntv.upgrade.rfnetworksimulator.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Paulino on 9/13/2015.
 */
public class TestUtilities extends AndroidTestCase {


    static ContentValues createNewSiteValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(SitesContract.SiteEntry.COLUMN_SITE_NAME, "North Pole");
        testValues.put(SitesContract.SiteEntry.COLUMN_SITE_LAT, 64.7488);
        testValues.put(SitesContract.SiteEntry.COLUMN_SITE_LNG, -147.353);
        testValues.put(SitesContract.SiteEntry.COLUMN_SITE_HEIGHT, 35.6);
        testValues.put(SitesContract.SiteEntry.COLUMN_SITE_STATUS, 1);

        return testValues;
    }

    static ContentValues createSectorValues(long siteRowId, String sectorName) {
        ContentValues sectorValues = new ContentValues();
        sectorValues.put(SitesContract.SectorEntry.COLUMN_SITE_KEY, siteRowId);
        sectorValues.put(SitesContract.SectorEntry.COLUMN_SECTOR_NAME, sectorName);
        sectorValues.put(SitesContract.SectorEntry.COLUMN_SECTOR_AZIMUTH, 90);
        sectorValues.put(SitesContract.SectorEntry.COLUMN_SECTOR_TILT, 1.2);

        return sectorValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
