package ntv.upgrade.rfnetworksimulator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.maps.model.LatLng;

import ntv.upgrade.rfnetworksimulator.site.Site;
import ntv.upgrade.rfnetworksimulator.tools.MathTools;

/**
 * Created by Paulino on 9/20/2015.
 */
public class EditSiteDialogFragment extends DialogFragment {

    private static final String ARG_SITE_LAT = "latitude";
    private static final String ARG_SITE_LNG = "longitude";
    private static final String ARG_ACTION_REQUEST = "edit/create";

    private Double mLat, mLng;
    private String mAction;
    private Site mSite = MainActivity.mSite;

    private OnFragmentInteractionListener mListener;

    public static EditSiteDialogFragment newInstance(Double lat, Double lng, String action) {
        EditSiteDialogFragment fragment = new EditSiteDialogFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_SITE_LAT, lat);
        args.putDouble(ARG_SITE_LNG, lng);
        args.putString(ARG_ACTION_REQUEST, action);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAction = getArguments().getString(ARG_ACTION_REQUEST);
            mLat = MathTools.round(getArguments().getDouble(ARG_SITE_LAT), 6);
            mLng = MathTools.round(getArguments().getDouble(ARG_SITE_LNG), 6);
        }
        mListener = (OnFragmentInteractionListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_site, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.tutorial_delete_site);

        final EditText siteName = (EditText) rootView.findViewById(R.id.siteName_Create);
        final Switch status = (Switch) rootView.findViewById(R.id.status_Create);
        final EditText latitude = (EditText) rootView.findViewById(R.id.siteLatitude_Create);
        final EditText longitude = (EditText) rootView.findViewById(R.id.siteLongitude_Create);
        final EditText height = (EditText) rootView.findViewById(R.id.siteHeight_Create);
        final EditText alphaAzimuth = (EditText) rootView.findViewById(R.id.alphaAzimuth_Create);
        final EditText alphaTilt = (EditText) rootView.findViewById(R.id.alphaTilt_Create);
        final EditText betaAzimuth = (EditText) rootView.findViewById(R.id.betaAzimuth_Create);
        final EditText betaTilt = (EditText) rootView.findViewById(R.id.betaTilt_Create);
        final EditText gammaAzimuth = (EditText) rootView.findViewById(R.id.gammaAzimuth_Create);
        final EditText gammaTilt = (EditText) rootView.findViewById(R.id.gammaTilt_Create);
        Button saveChanges = (Button) rootView.findViewById(R.id.saveChanges_Create);
        Button cancelChanges = (Button) rootView.findViewById(R.id.cancelChanges_Create);

        latitude.setText(mLat.toString());
        longitude.setText(mLng.toString());

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getDialog().getWindow().setBackgroundDrawableResource(R.color.tutorial_add_new_site);
                } else
                    getDialog().getWindow().setBackgroundDrawableResource(R.color.tutorial_delete_site);
            }
        });

        if (mAction.equals("edit")) {
            siteName.setText(mSite.getName());
            latitude.setText(Double.toString(mSite.getPosition().latitude));
            longitude.setText(Double.toString(mSite.getPosition().longitude));
            height.setText(Double.toString(MathTools.round(mSite.getHeight() * 1000, 4)));
            status.setChecked(mSite.getStatus());
            alphaAzimuth.setText(Integer.toString(mSite.getAlpha().getAzimuth()));
            alphaTilt.setText(Double.toString(mSite.getAlpha().getTilt()));
            betaAzimuth.setText(Integer.toString(mSite.getBeta().getAzimuth()));
            betaTilt.setText(Double.toString(mSite.getBeta().getTilt()));
            gammaAzimuth.setText(Integer.toString(mSite.getGamma().getAzimuth()));
            gammaTilt.setText(Double.toString(mSite.getGamma().getTilt()));
        }

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSite.setName(siteName.getText().toString());
                mSite.setStatus(status.isChecked());
                mSite.setPosition(new LatLng(
                        Double.parseDouble(latitude.getText().toString()),
                        Double.parseDouble(longitude.getText().toString())));
                mSite.setHeight(Double.parseDouble(height.getText().toString()) / 1000);
                mSite.getAlpha().setAzimuth(Integer.parseInt(alphaAzimuth.getText().toString()));
                mSite.getAlpha().setTilt(Double.parseDouble(alphaTilt.getText().toString()));
                mSite.getBeta().setAzimuth(Integer.parseInt(betaAzimuth.getText().toString()));
                mSite.getBeta().setTilt(Double.parseDouble(betaTilt.getText().toString()));
                mSite.getGamma().setAzimuth(Integer.parseInt(gammaAzimuth.getText().toString()));
                mSite.getGamma().setTilt(Double.parseDouble(gammaTilt.getText().toString()));

                mListener.onSaveButtonClicked(mAction);
                getDialog().dismiss();
            }
        });
        cancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    public interface OnFragmentInteractionListener {
        void onSaveButtonClicked(String action);
    }
}
