package ntv.upgrade.rfnetworksimulator.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.security.PublicKey;

/**
 * Defines table and column names for the sites database
 *
 * Created by Paulino on 9/13/2015.
 */
public class SitesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "ntv.upgrade.rfnetworksimulator";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://ntv.upgrade.rfnetworksimulator/sites/ is a valid path for
    // looking at sites data.
    public static final String PATH_SITE = "site";
    public static final String PATH_SECTOR = "sector";


    /* Inner class that defines the table contents of the site table */
    public static final class SiteEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SITE;

        // Table name
        public static final String TABLE_NAME = "site";

        // Table Columns
        public static final String COLUMN_SITE_NAME = "site_name";
        public static final String COLUMN_SITE_LAT = "site_lat";
        public static final String COLUMN_SITE_LNG = "site_lng";
        public static final String COLUMN_SITE_HEIGHT = "site_height";
        public static final String COLUMN_SITE_STATUS = "site_status";

        // Used to insert a new Site into the db
        public static Uri buildSiteUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSiteWithSiteName(String siteName){
            return CONTENT_URI.buildUpon().appendPath(siteName).build();
        }

        public static String getSiteNameFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

    }

    /* Inner class that defines the table contents of the sector table */
    public static final class SectorEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SECTOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECTOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SECTOR;

        // Table name
        public static final String TABLE_NAME = "sector";

        // Column with the foreign key into the location table.
        public static final String COLUMN_SITE_KEY = "site_id";

        // Table columns
        public static final String COLUMN_SECTOR_NAME = "sector_name";
        public static final String COLUMN_SECTOR_AZIMUTH = "azimuth";
        public static final String COLUMN_SECTOR_TILT = "tilt";

        public static Uri buildSectorUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }



    }

}
