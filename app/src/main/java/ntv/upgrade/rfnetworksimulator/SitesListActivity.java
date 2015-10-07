package ntv.upgrade.rfnetworksimulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import ntv.upgrade.rfnetworksimulator.dummy.ListAdapter;
import ntv.upgrade.rfnetworksimulator.site.Site;

public class SitesListActivity extends AppCompatActivity {

    private ListAdapter mSitesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: 9/29/2015 Replace fab with some functionality
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSitesListAdapter = new ListAdapter(this, R.layout.site_view_layout, MainActivity.mSitesArrayList);


        if (this.findViewById(R.id.listview_sites_list) != null) {
            ListView sitesListView = (ListView) this.findViewById(R.id.listview_sites_list);
            sitesListView.setAdapter(mSitesListAdapter);
            sitesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Site tempSite = mSitesListAdapter.getItem(position);
                    goToSiteOnMap(tempSite.getGeo());


                }
            });
        } else {
            GridView sitesGridView = (GridView) this.findViewById(R.id.gridview_sites_list);
            sitesGridView.setAdapter(mSitesListAdapter);
            sitesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Site tempSite = mSitesListAdapter.getItem(position);
                    goToSiteOnMap(tempSite.getGeo());

                }
            });
        }
    }

    private void goToSiteOnMap(LatLng geo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("geo", geo);
        startActivity(intent);
    }
}
