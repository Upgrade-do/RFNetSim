package ntv.upgrade.rfnetworksimulator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import ntv.upgrade.rfnetworksimulator.dummy.ListAdapter;

public class SitesListActivity extends AppCompatActivity {

    private ListAdapter mSitesListAdapter;
    private Boolean mLargeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSitesListAdapter = new ListAdapter(this, R.layout.list_item_site, MainActivity.mSitesArrayList);


        if (this.findViewById(R.id.listview_sites_list) != null) {
            ListView sitesListView = (ListView) this.findViewById(R.id.listview_sites_list);
            sitesListView.setAdapter(mSitesListAdapter);
        } else {
            GridView sitesGridView = (GridView) this.findViewById(R.id.gridview_sites_list);
            sitesGridView.setAdapter(mSitesListAdapter);
        }

    }


}
