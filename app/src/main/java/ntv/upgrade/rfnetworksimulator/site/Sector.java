package ntv.upgrade.rfnetworksimulator.site;

import com.google.android.gms.maps.model.LatLng;

/**
 * Sector base class, defines a sector, that it, the area covered
 * by one of the tree{3} radio frequency transmitters of an antenna.
 *
 * Created by Paulino on 9/16/2015.
 */
public class Sector {

    /*
     * at this point we assume all sectors have a fixed mechanical
     * tilt of 2 degrees
     */
    private static final long ELECTRICAL_TILT = 2;
    // earth radius in KM
    private static final long EARTH_RADIUS = 6367;
    //
    private static final int RANGE_WITH = 60;

    /**
     * Member variables to define area covered by a sector
     */
    private LatLng geo = null;
    private LatLng p1 = null;
    private LatLng p2 = null;
    private double height = -1;
    private double mTilt = -1;
    private double range = -1;
    private int azimuth;

    /**
     * Constructor to set a new Sector
     */
    public Sector(LatLng geo, int azimuth, double mTilt, double height) {
        setGeo(geo);
        setAzimuth(azimuth);
        setTilt(mTilt);
        setHeight(height);
        calculateRange();
        recalculate();
    }

    /**
     * Assigns/modifies geolocation of current sector
     *
     * @param geo is used as the new geolocation for the sector
     *            and recalculates since p1 & p2 depends on geolocation
     */
    public void setGeo(LatLng geo) {
        this.geo = geo;
        recalculate();
    }

    /**
     * Assigns/modifies height value of the sector
     *
     * @param height is assigned as the new sector height
     *               and recalculates range distance and p1 & p2
     *               since those parameters depends on height
     */
    protected void setHeight(double height) {
        this.height = height / 1000;
        calculateRange();
        recalculate();
    }

    /**
     * @return value of p1
     */
    public LatLng getP1() {
        return p1;
    }

    /**
     * @return value of p1
     */
    public LatLng getP2() {
        return p2;
    }

    /**
     * @return value of sector tilt, that it, the combination of mTilt
     * and ELECTRICAL_TILT
     */
    public double getTilt() {
        return mTilt + ELECTRICAL_TILT;
    }

    /**
     * Assigns/modifies tilt value of the sector
     *
     * @param mTilt is assigned as the new sector mechanical tilt
     *              and recalculates range distance and p1 & p2
     *              since those parameters depends on tilt
     */
    public void setTilt(double mTilt) {
        this.mTilt = mTilt - ELECTRICAL_TILT;
        calculateRange();
        recalculate();
    }

    /**
     * @return value of azimuth
     */
    public int getAzimuth() {
        return azimuth;
    }

    /**
     * Assigns/modifies azimuth value of the sector
     *
     * @param azimuth is assigned as the new sector azimuth
     *                and recalculates since p1 & p2 depends on azimuth
     */
    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
        recalculate();
    }

    // // TODO: 9/27/2015 Discover what does this function do, and comment it to de detail.
    private void calculateRange() {
        this.range = (height) / Math.sin(Math.toRadians(getTilt()));
    }

    /**
     * Recalculates p1 & p2 due to a change on one of the values
     * they depend on (geo, range, height, azimuth)
     */
    private void recalculate() {

        double lat = Math.toRadians(this.geo.latitude);
        double lon = Math.toRadians(this.geo.longitude);
        double centralAngle = this.range / EARTH_RADIUS;

        // Lat & Lng for P1
        double latP1 = Math.asin(Math.sin(lat) * Math.cos(centralAngle)
                + Math.cos(lat) * Math.sin(centralAngle)
                * Math.cos(Math.toRadians(angleForP1())));

        double lonP1 = lon + Math.atan2(Math.sin(Math.toRadians(angleForP1()))
                * Math.sin(centralAngle) * Math.cos(lat), Math.cos(centralAngle)
                - Math.sin(lat) * Math.sin(lat));

        // Lat & Lng for P2
        double latP2 = Math.asin(Math.sin(lat) * Math.cos(centralAngle)
                + Math.cos(lat) * Math.sin(centralAngle)
                * Math.cos(Math.toRadians(angleForP2())));

        double lonP2 = lon + Math.atan2(Math.sin(Math.toRadians(angleForP2()))
                * Math.sin(centralAngle) * Math.cos(lat), Math.cos(centralAngle)
                - Math.sin(lat) * Math.sin(lat));

        this.p1 = new LatLng(Math.toDegrees(latP1), Math.toDegrees(lonP1));
        this.p2 = new LatLng(Math.toDegrees(latP2), Math.toDegrees(lonP2));

    }

    /**
     * helper function for recalculate().
     */
    private int angleForP1() {
        if (this.azimuth - (RANGE_WITH / 2) < 0) {
            return this.azimuth + 360 - (RANGE_WITH / 2);
        }
        return this.azimuth - (RANGE_WITH / 2);
    }

    /**
     * helper function for recalculate().
     */
    private int angleForP2() {
        if (this.azimuth + (RANGE_WITH / 2) > 360) {
            return this.azimuth - 360 - (RANGE_WITH / 2);
        }
        return this.azimuth + (RANGE_WITH / 2);
    }
}