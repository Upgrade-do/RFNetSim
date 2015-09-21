package ntv.upgrade.rfnetworksimulator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ntv.upgrade.rfnetworksimulator.dummy.DummySiteList;
import ntv.upgrade.rfnetworksimulator.site.Site;

public class SitesListActivity extends AppCompatActivity {

    private ArrayAdapter<Site> mSitesListAdapter;

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

        mSitesListAdapter =
                new ArrayAdapter<Site>(
                        this, // The current context (this activity)
                        R.layout.list_item_site, // The name of the layout ID.
                        R.id.textView_Site_Name, // The ID of the textview to populate.
                        DummySiteList.mSitesArrayList);


        ListView listView = (ListView) this.findViewById(R.id.listview_sites_list);
        listView.setAdapter(mSitesListAdapter);
    }


}
