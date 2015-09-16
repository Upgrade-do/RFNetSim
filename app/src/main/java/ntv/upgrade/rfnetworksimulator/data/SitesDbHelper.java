package ntv.upgrade.rfnetworksimulator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ntv.upgrade.rfnetworksimulator.data.SitesContract.SiteEntry;
import ntv.upgrade.rfnetworksimulator.data.SitesContract.SectorEntry;

/**
 * Manages a local database for Sites data
 *
 * Created by Paulino on 9/13/2015.
 */
public class SitesDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you MUST increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "sites.db";

    public SitesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables happens.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold sites. A site consists of a site name, site lat, site lng
        // site height, and status
        final String SQL_CREATE_SITE_TABLE =
                "CREATE TABLE " + SiteEntry.TABLE_NAME + " (" +
                        SiteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SiteEntry.COLUMN_SITE_NAME + " TEXT UNIQUE NOT NULL, " +
                        SiteEntry.COLUMN_SITE_LAT + " REAL NOT NULL, " +
                        SiteEntry.COLUMN_SITE_LNG + " REAL NOT NULL, " +
                        SiteEntry.COLUMN_SITE_HEIGHT + " REAL NOT NULL, " +
                        SiteEntry.COLUMN_SITE_STATUS + " REAL NOT NULL " +
                        ")";

        final String SQL_CREATE_SECTOR_TABLE =
                "CREATE TABLE " + SectorEntry.TABLE_NAME + " (" +
                        SectorEntry._ID + " INTEGER PRIMARY KEY, " +
                        SectorEntry.COLUMN_SITE_KEY + " INTEGER NOT NULL, " +
                        SectorEntry.COLUMN_SECTOR_NAME + " TEXT NOT NULL, " +
                        SectorEntry.COLUMN_SECTOR_AZIMUTH + " REAL NOT NULL, " +
                        SectorEntry.COLUMN_SECTOR_TILT + " REAL NOT NULL, " +

                        // Set up the site key column as a foreign key to site table.
                        "FOREIGN KEY (" + SectorEntry.COLUMN_SITE_KEY +
                        ") REFERENCES " + SiteEntry.TABLE_NAME + " (" + SiteEntry._ID + "))";

        db.execSQL(SQL_CREATE_SITE_TABLE);
        db.execSQL(SQL_CREATE_SECTOR_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // This only fires if the version number for the database changes.
        db.execSQL("DROP TABLE IF EXISTS " + SiteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SectorEntry.TABLE_NAME);
        onCreate(db);
    }
}
