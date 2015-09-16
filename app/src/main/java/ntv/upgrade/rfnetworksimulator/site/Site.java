package ntv.upgrade.rfnetworksimulator.site;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Paulino on 9/16/2015.
 */
public class Site {

    private int siteId;
    private String name;
    private LatLng position;
    private double height;
    private boolean status;
    private Sector alpha, beta, gamma;

    public Site() {
        this.alpha = new Sector();
        this.beta = new Sector();
        this.gamma = new Sector();
    }

    public Site(int siteId, String name, LatLng position, double height, boolean status,
                int alphaAzimuth, double alphaTilt,
                int betaAzimuth, double betaTilt,
                int gammaAzimuth, double gammaTilt) {

        this.siteId = siteId;
        this.name = name;
        this.position = position;
        this.height = height / 1000;
        this.status = status;
        this.alpha = new Sector(position, alphaAzimuth, alphaTilt, this.height);
        this.beta = new Sector(position, betaAzimuth, betaTilt, this.height);
        this.gamma = new Sector(position, gammaAzimuth, gammaTilt, this.height);
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(LatLng position) {
        this.position = position;
        this.alpha.setAntenna(position);
        this.beta.setAntenna(position);
        this.gamma.setAntenna(position);
    }

    public void setHeight(double height) {
        this.height = height;
        this.alpha.setHeight(height);
        this.beta.setHeight(height);
        this.gamma.setHeight(height);
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    //Getters
    public int getSiteId() {
        return siteId;
    }

    public String getName() {
        return name;
    }

    public LatLng getPosition() {
        return position;
    }

    public boolean getStatus() {
        return status;
    }

    public double getHeight() {
        return height;
    }

    public Sector getAlpha() {
        return alpha;
    }

    public Sector getBeta() {
        return beta;
    }

    public Sector getGamma() {
        return gamma;
    }
}
