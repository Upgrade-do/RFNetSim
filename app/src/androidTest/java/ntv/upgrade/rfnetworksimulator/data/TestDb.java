package ntv.upgrade.rfnetworksimulator.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Paulino on 9/13/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean state
    void deleteTheDatabase(){
        mContext.deleteDatabase(SitesDbHelper.DATABASE_NAME);
    }

    /**
     * This function gets called before each test is executed to delete the database.
     * This makes sure that we always have a clean test.
     */
    public void setUp(){
        deleteTheDatabase();
    }

    /**
     *
     */
    public void testCreateDb() throws Throwable{
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(SitesContract.SiteEntry.TABLE_NAME);
        tableNameHashSet.add(SitesContract.SectorEntry.TABLE_NAME);

        mContext.deleteDatabase(SitesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new SitesDbHelper(this.mContext)
                .getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Have we created the tables we want?
        Cursor cursor =
                db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while( cursor.moveToNext() );

        // if this fails, it means that your database doesn't contain both the sites entry
        // and sectors entry tables
        assertTrue("Error: Your database was created without both the sites entry and sectors entry tables",
                tableNameHashSet.isEmpty());

        // now, do our site table contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + SitesContract.SiteEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> siteColumnHashSet = new HashSet<>();
        siteColumnHashSet.add(SitesContract.SiteEntry._ID);
        siteColumnHashSet.add(SitesContract.SiteEntry.COLUMN_SITE_NAME);
        siteColumnHashSet.add(SitesContract.SiteEntry.COLUMN_SITE_LAT);
        siteColumnHashSet.add(SitesContract.SiteEntry.COLUMN_SITE_LNG);
        siteColumnHashSet.add(SitesContract.SiteEntry.COLUMN_SITE_HEIGHT);
        siteColumnHashSet.add(SitesContract.SiteEntry.COLUMN_SITE_STATUS);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            siteColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                siteColumnHashSet.isEmpty());

        // now, do our sector table contains the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + SitesContract.SectorEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> sectorColumnHashSet = new HashSet<>();
        sectorColumnHashSet.add(SitesContract.SectorEntry._ID);
        sectorColumnHashSet.add(SitesContract.SectorEntry.COLUMN_SITE_KEY);
        sectorColumnHashSet.add(SitesContract.SectorEntry.COLUMN_SECTOR_NAME);
        sectorColumnHashSet.add(SitesContract.SectorEntry.COLUMN_SECTOR_AZIMUTH);
        sectorColumnHashSet.add(SitesContract.SectorEntry.COLUMN_SECTOR_TILT);

        columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            sectorColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                sectorColumnHashSet.isEmpty());
        db.close();
    }

    /**
     *
     */
    public void testSiteTable(){
        insertSite();
    }

    public void testSectorTable(){
        // First insert the site, and then use the siteRowId to insert
        // the sector. Make sure to cover as many failure cases as you can.
        long siteRowId = insertSite();

        //Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", siteRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        SitesDbHelper dbHelper = new SitesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Sector): Create weather values
        ContentValues sectorValues = TestUtilities.createSectorValues(siteRowId, "Alpha");

        // Third Step (Sector): Insert ContentValues into database and get a row ID back
        long sectorRowId = db.insert(SitesContract.SectorEntry.TABLE_NAME, null, sectorValues);
        assertTrue(sectorRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor sectorCursor = db.query(
                SitesContract.SectorEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", sectorCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                sectorCursor, sectorValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                sectorCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        sectorCursor.close();
        dbHelper.close();

    }

    /**
     *
     */
    public long insertSite(){
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        SitesDbHelper dbHelper = new SitesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createNewSiteValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long siteRowId;
        siteRowId = db.insert(SitesContract.SiteEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(siteRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                SitesContract.SiteEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return siteRowId;
    }
}
