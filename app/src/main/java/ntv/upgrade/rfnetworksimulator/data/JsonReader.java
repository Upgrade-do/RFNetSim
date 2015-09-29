package ntv.upgrade.rfnetworksimulator.data;

import android.util.JsonToken;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ntv.upgrade.rfnetworksimulator.site.Sector;
import ntv.upgrade.rfnetworksimulator.site.Site;

/**
 * Created by Paulino on 9/27/2015.
 */
public class JsonReader {

    public ArrayList<Site> readJsonStream(InputStream in) throws IOException {
        android.util.JsonReader reader = new android.util.JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readSitesArray(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Site> readSitesArray(android.util.JsonReader reader) throws IOException {
        ArrayList<Site> sitesList = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            sitesList.add(readSite(reader));
        }
        reader.endArray();
        return sitesList;
    }

    private Site readSite(android.util.JsonReader reader) throws IOException {
        String name = null;
        List geo = null;
        double height = 0;
        boolean isOperational = false;
        Sector alpha = null;
        Sector beta = null;
        Sector gamma = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String field = reader.nextName();
            if (field.equals("name")) {
                name = reader.nextString();
            } else if (field.equals("geo") && reader.peek() != JsonToken.NULL) {
                geo = readGeoLocation(reader);
            } else if (field.equals("height")) {
                height = reader.nextDouble();
            } else if (field.equals("status")) {
                isOperational = reader.nextBoolean();
            } else if (field.equals("alpha")) {
                alpha = readSector(reader, geo, height);
            } else if (field.equals("beta")) {
                beta = readSector(reader, geo, height);
            } else if (field.equals("gamma")) {
                gamma = readSector(reader, geo, height);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        LatLng latLng = new LatLng(
                Double.parseDouble(geo.get(0).toString()),
                Double.parseDouble(geo.get(1).toString()));

        return new Site(name, latLng, height, isOperational
                , alpha, beta, gamma);
    }

    private List readGeoLocation(android.util.JsonReader reader) throws IOException {
        List geo = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            geo.add(reader.nextDouble());
        }
        reader.endArray();
        return geo;
    }

    private Sector readSector(android.util.JsonReader reader, List geo, double height) throws IOException {
        int azimuth = -1;
        double tilt = -1;

        LatLng latLng = new LatLng(
                Double.parseDouble(geo.get(0).toString()),
                Double.parseDouble(geo.get(1).toString()));

        reader.beginObject();
        while (reader.hasNext()) {
            String field = reader.nextName();
            if (field.equals("azimuth")) {
                azimuth = reader.nextInt();
            } else if (field.equals("tilt")) {
                tilt = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Sector(latLng, azimuth, tilt, height);
    }

}

