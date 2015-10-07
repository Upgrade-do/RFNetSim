package ntv.upgrade.rfnetworksimulator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;

import ntv.upgrade.rfnetworksimulator.site.Sector;
import ntv.upgrade.rfnetworksimulator.site.Site;

/**
 *
 * Created by Paulino on 9/20/2015.
 */
public class EditSiteDialogFragment extends DialogFragment {

    private static final String ARG_ACTION_REQUEST = "edit/create";

    EditText siteName, latitude, longitude, height,
            alphaAzimuth, betaAzimuth, gammaAzimuth,
            alphaTilt, betaTilt, gammaTilt;
    Switch isOperational;

    private String mAction;

    private Site tempSite = null;
    private LatLng tempGeolocation = null;

    private OnFragmentInteractionListener mListener;

    public static EditSiteDialogFragment newInstance(String actionRequest) {
        EditSiteDialogFragment fragment = new EditSiteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION_REQUEST, actionRequest);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAction = getArguments().getString(ARG_ACTION_REQUEST);
        }
        mListener = (OnFragmentInteractionListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.site_edit_layout, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        siteName = (EditText) rootView.findViewById(R.id.siteName_Create);
        isOperational = (Switch) rootView.findViewById(R.id.status_Create);

        latitude = (EditText) rootView.findViewById(R.id.siteLatitude_Create);
        longitude = (EditText) rootView.findViewById(R.id.siteLongitude_Create);
        height = (EditText) rootView.findViewById(R.id.siteHeight_Create);

        alphaAzimuth = (EditText) rootView.findViewById(R.id.alphaAzimuth_Create);
        betaAzimuth = (EditText) rootView.findViewById(R.id.betaAzimuth_Create);
        gammaAzimuth = (EditText) rootView.findViewById(R.id.gammaAzimuth_Create);

        alphaTilt = (EditText) rootView.findViewById(R.id.alphaTilt_Create);
        betaTilt = (EditText) rootView.findViewById(R.id.betaTilt_Create);
        gammaTilt = (EditText) rootView.findViewById(R.id.gammaTilt_Create);

        ImageButton saveChanges = (ImageButton) rootView.findViewById(R.id.saveChanges_Create);
        ImageButton cancelChanges = (ImageButton) rootView.findViewById(R.id.cancelChanges_Create);

        isOperational.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    siteName.setBackgroundResource(R.color.colorGreen);
                } else
                    siteName.setBackgroundResource(R.color.colorRed);
            }
        });

        loadDataToUi();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempName;
                if (siteName.getText().toString().equals("")) {
                    tempName = (getResources().getString(R.string.site_with_no_name));
                } else tempName = siteName.getText().toString();

                LatLng tempGeo = new LatLng(
                        Double.parseDouble(latitude.getText().toString()),
                        Double.parseDouble(longitude.getText().toString()));
                double tempHeight = Double.parseDouble(height.getText().toString());
                boolean tempStatus = isOperational.isChecked();
                Sector tempAlpha = new Sector(tempGeo,
                        Integer.parseInt(alphaAzimuth.getText().toString()),
                        Double.parseDouble(alphaTilt.getText().toString()),
                        tempHeight);
                Sector tempBeta = new Sector(tempGeo,
                        Integer.parseInt(betaAzimuth.getText().toString()),
                        Double.parseDouble(betaTilt.getText().toString()),
                        tempHeight);
                Sector tempGamma = new Sector(tempGeo,
                        Integer.parseInt(gammaAzimuth.getText().toString()),
                        Double.parseDouble(gammaTilt.getText().toString()),
                        tempHeight);

                tempSite = new Site(tempName, tempGeo, tempHeight, tempStatus, tempAlpha, tempBeta, tempGamma);

                if (mAction.equals("edit")) {
                    MainActivity.mSitesArrayList.remove(MainActivity.tempSite);
                }

                MainActivity.tempSite = tempSite;
                tempGeolocation = null;
                tempSite = null;
                mListener.onSaveButtonClicked(mAction);
                getDialog().dismiss();
            }
        });
        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempGeolocation = null;
                tempSite = null;
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    private boolean loadDataToUi() {
        switch (mAction) {
            case "create":
                tempGeolocation = MainActivity.tempGeolocation;

                latitude.setText(String.format("%.6f", tempGeolocation.latitude));
                longitude.setText(String.format("%.6f", tempGeolocation.longitude));

                tempSite = MainActivity.tempSite;
                break;
            case "edit":
                tempSite = MainActivity.tempSite;

                siteName.setText(tempSite.getName());
                height.setText(String.format("%.2f", tempSite.getHeight()));
                isOperational.setChecked(tempSite.isOperational());

                latitude.setText(String.format("%.6f", tempSite.getGeo().latitude));
                longitude.setText(String.format("%.6f", tempSite.getGeo().longitude));

                alphaAzimuth.setText(String.format("%d", tempSite.getAlpha().getAzimuth()));
                betaAzimuth.setText(String.format("%d", tempSite.getBeta().getAzimuth()));
                gammaAzimuth.setText(String.format("%d", tempSite.getGamma().getAzimuth()));

                alphaTilt.setText(String.format("%.1f", tempSite.getAlpha().getTilt()));
                betaTilt.setText(String.format("%.1f", tempSite.getBeta().getTilt()));
                gammaTilt.setText(String.format("%.1f", tempSite.getGamma().getTilt()));
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    public interface OnFragmentInteractionListener {
        void onSaveButtonClicked(String action);
    }
}
