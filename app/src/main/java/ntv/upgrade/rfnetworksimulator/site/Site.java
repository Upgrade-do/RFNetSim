package ntv.upgrade.rfnetworksimulator.site;

import com.google.android.gms.maps.model.LatLng;

/**
 * Site base class, defines a site with 3 sectors, that is,
 * an Antenna with id, name, Geolocation, height and whether
 * or not the site is is operational, plus tree {3} sectors.
 *
 * Created by Paulino on 9/16/2015.
 */
public class Site {

    /**
     * Member variables to define a site
     */
    private String name = null;
    private LatLng geo = null;
    private double height = -1;
    private boolean isOperational = false;
    private Sector alpha = null;
    private Sector beta = null;
    private Sector gamma = null;

    /**
     * Constructor to set a new site
     */
    public Site(String name, LatLng geo, Double height, boolean isOperational,
                Sector alpha, Sector beta, Sector gamma) {

        setName(name);
        setGeo(geo);
        setHeight(height);
        if (isOperational != isOperational()) {
            toggleOperationalStatus();
        }
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    /**
     * Assigns/modifies geolocation to the site and its tree sectors.
     * Verifies all tree sectors exists to avoid null pointer exception.
     *
     * @param geo is used as the new geolocation for the site
     * @return true if geolocation was successfully assigned to
     * the site and all tree sectors, or false otherwise.
     */
    public boolean setGeo(LatLng geo) {
        this.geo = geo;

        if (this.alpha != null && this.beta != null && this.gamma != null) {
            this.alpha.setGeo(getGeo());
            this.beta.setGeo(getGeo());
            this.gamma.setGeo(getGeo());
        } else {
            return false;
        }
        return true;
    }

    /**
     * Assigns/modifies height on the site and its tree sectors.
     * Verifies all tree sectors exists to avoid null pointer exception.
     *
     * @param height is used as the new height for the site.
     * @return true if height was successfully assigned to
     * the site and all tree sectors, or false otherwise.
     */
    public boolean setHeight(double height) {
        this.height = height / 1000;

        if (this.alpha != null && this.beta != null && this.gamma != null) {
            this.alpha.setHeight(height);
            this.beta.setHeight(height);
            this.gamma.setHeight(height);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Toggles operational status of the site (on/off)
     */
    public void toggleOperationalStatus() {
        this.isOperational = !this.isOperational;
    }

    /**
     * @return name of the current Site
     */
    public String getName() {
        return this.name;
    }

    /**
     * Assigns/modifies name value of the site
     *
     * @param name is assigned as the new site name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return geolocation of the current Site
     */
    public LatLng getGeo() {
        return this.geo;
    }

    /**
     * @return whether or not the Site is operational
     */
    public boolean isOperational() {
        return this.isOperational;
    }

    /**
     * @return height of the current Site
     */
    public double getHeight() {
        return this.height * 1000;
    }

    /**
     * @return sector Alpha of the current Site
     */
    public Sector getAlpha() {
        return this.alpha;
    }

    /**
     * @return sector Beta of the current Site
     */
    public Sector getBeta() {
        return this.beta;
    }

    /**
     * @return sector Gamma of the current Site
     */
    public Sector getGamma() {
        return this.gamma;
    }
}
