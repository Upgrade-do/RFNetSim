package ntv.upgrade.rfnetworksimulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabContactUs = (FloatingActionButton) findViewById(R.id.fab_contact_us);
        fabContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, R.string.dev_email);
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject);
                intent.putExtra(Intent.EXTRA_TEXT, R.string.email_body);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
