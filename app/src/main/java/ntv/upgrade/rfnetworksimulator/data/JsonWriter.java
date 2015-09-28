package ntv.upgrade.rfnetworksimulator.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import ntv.upgrade.rfnetworksimulator.site.Sector;
import ntv.upgrade.rfnetworksimulator.site.Site;

/**
 * Json Writer
 * <p/>
 * Created by Paulino on 9/26/2015.
 */
public class JsonWriter {

    // TODO: 9/27/2015 Comment this class
    public void writeJsonStream(OutputStream out, ArrayList<Site> sitesList) throws IOException {
        android.util.JsonWriter writer = new android.util.JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("  ");
        writeSitesArray(writer, sitesList);
        writer.close();
    }

    private void writeSitesArray(android.util.JsonWriter writer, List<Site> sitesList) throws IOException {
        writer.beginArray();
        for (Site site : sitesList) {
            writeSite(writer, site);
        }
        writer.endArray();
    }

    private void writeSite(android.util.JsonWriter writer, Site site) throws IOException {
        writer.beginObject();

        writer.name("name").value(site.getName());

        writer.name("geo");
        writeGeoLocation(writer, site.getGeo());
        writer.name("height").value(site.getHeight());
        writer.name("status").value(site.isOperational());

        writer.name("alpha");
        writeSector(writer, site.getAlpha());
        writer.name("beta");
        writeSector(writer, site.getBeta());
        writer.name("gamma");
        writeSector(writer, site.getGamma());

        writer.endObject();
    }

    private void writeSector(android.util.JsonWriter writer, Sector sector) throws IOException {
        writer.beginObject();
        writer.name("azimuth").value(sector.getAzimuth());
        writer.name("tilt").value(sector.getTilt());
        writer.endObject();
    }

    private void writeGeoLocation(android.util.JsonWriter writer, LatLng latLng) throws IOException {
        writer.beginArray();
        writer.value(latLng.latitude);
        writer.value(latLng.longitude);
        writer.endArray();
    }


}
