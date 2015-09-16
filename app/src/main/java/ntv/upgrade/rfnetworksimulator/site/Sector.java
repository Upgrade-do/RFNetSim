package ntv.upgrade.rfnetworksimulator.site;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Paulino on 9/16/2015.
 */
public class Sector {

    private LatLng antenna, p1, p2;
    private double height, tilt, range;
    private int azimuth;

    public Sector() {
    }

    public Sector(LatLng antenna, int azimuth, double tilt, double height) {
        this.antenna = antenna;
        this.azimuth = azimuth;
        this.tilt = tilt + 2;
        this.height = height;
        calculateRange();
        recalculate();
    }

    public void setAntenna(LatLng antenna) {
        this.antenna = antenna;
        recalculate();
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
        recalculate();
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
        calculateRange();
        recalculate();
    }

    public void setHeight(double height) {
        this.height = height;
        calculateRange();
        recalculate();
    }

    //Getters
    public LatLng getP1() {
        return p1;
    }

    public LatLng getP2() {
        return p2;
    }

    public double getTilt() {
        return tilt;
    }

    public int getAzimuth() {
        return azimuth;
    }

    private void recalculate() {

        double lat = Math.toRadians(this.antenna.latitude);
        double lon = Math.toRadians(this.antenna.longitude);
        double centralAngle = this.range / 6367d;

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

    private void calculateRange() {
        this.range = height / Math.sin(Math.toRadians(this.tilt));
    }

    private int angleForP1() {
        if (this.azimuth - 30 < 0) {
            return this.azimuth + 330;
        }
        return this.azimuth - 30;
    }

    private int angleForP2() {
        if (this.azimuth + 30 > 360) {
            return this.azimuth - 330;
        }
        return this.azimuth + 30;
    }
}