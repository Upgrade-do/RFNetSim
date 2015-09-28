package ntv.upgrade.rfnetworksimulator.dummy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ntv.upgrade.rfnetworksimulator.R;
import ntv.upgrade.rfnetworksimulator.site.Site;

/**
 * Created by Paulino on 9/23/2015.
 */
public class ListAdapter extends ArrayAdapter<Site> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Site> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_site, null);
        }

        Site site = getItem(position);

        if (site != null) {
            TextView siteName = (TextView) v.findViewById(R.id.textView_Site_Name);
            TextView latLng = (TextView) v.findViewById(R.id.textView_Lat_Lng);
            TextView heigh = (TextView) v.findViewById(R.id.textView_heigh);

            TextView alphaAzimuth = (TextView) v.findViewById(R.id.alphaAzimuth_textView);
            TextView betaAzimuth = (TextView) v.findViewById(R.id.betaAzimuth_textView);
            TextView gammaAzimuth = (TextView) v.findViewById(R.id.gammaAzimuth_textView);

            TextView alphaTilt = (TextView) v.findViewById(R.id.alphaTilt_textView);
            TextView betaTilt = (TextView) v.findViewById(R.id.betaTilt_textView);
            TextView gammaTilt = (TextView) v.findViewById(R.id.gammaTilt_textView);

            if (siteName != null) {
                siteName.setText(site.getName());
            }
            if (latLng != null) {
                latLng.setText(String.format("(%.6f, %.6f)", site.getGeo().latitude, site.getGeo().longitude));
            }
            if (heigh != null) {
                heigh.setText(String.format("Heigh: %.2f mts", site.getHeight()));
            }
            if (alphaAzimuth != null) {
                alphaAzimuth.setText(String.format("%d", site.getAlpha().getAzimuth()));
            }
            if (betaAzimuth != null) {
                betaAzimuth.setText(String.format("%d", site.getBeta().getAzimuth()));
            }
            if (gammaAzimuth != null) {
                gammaAzimuth.setText(String.format("%d", site.getGamma().getAzimuth()));
            }
            if (alphaTilt != null) {
                alphaTilt.setText(String.format("%.1f", site.getAlpha().getTilt()));
            }
            if (betaTilt != null) {
                betaTilt.setText(String.format("%.1f", site.getBeta().getTilt()));
            }
            if (gammaTilt != null) {
                gammaTilt.setText(String.format("%.1f", site.getGamma().getTilt()));
            }
        }
        return v;
    }

}