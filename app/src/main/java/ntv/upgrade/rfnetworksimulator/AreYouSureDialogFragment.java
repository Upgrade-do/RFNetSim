package ntv.upgrade.rfnetworksimulator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 *
 * Created by Paulino on 9/20/2015.
 */
public class AreYouSureDialogFragment extends DialogFragment {

    // TODO: 9/27/2015 comment this class
    private static final String ARG_ACTION_REQUEST = "create/delete";

    private String mAction;

    private OnFragmentInteractionListener mListener;

    public static AreYouSureDialogFragment newInstance(String action) {
        AreYouSureDialogFragment fragment = new AreYouSureDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION_REQUEST, action);
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
        View rootView = inflater.inflate(R.layout.do_you_wanna, container, false);

        TextView title = (TextView) getDialog().findViewById(android.R.id.title);
        title.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

        TextView doYouWannaTextView = (TextView) rootView.findViewById(R.id.doYouWannaTextView);
        ImageButton acceptButton = (ImageButton) rootView.findViewById(R.id.acceptButton);
        ImageButton declineButton = (ImageButton) rootView.findViewById(R.id.declineButton);

        if (mAction.equals("create")) {
            getDialog().setTitle("Create new Site");
            doYouWannaTextView.setText(
                    String.format(getResources().getString(R.string.creating_new_site),
                            MainActivity.tempGeolocation.latitude,
                            MainActivity.tempGeolocation.longitude));
        } else {
            getDialog().setTitle("Delete Site");
            doYouWannaTextView.setText(Html.fromHtml(String.format(
                    getResources().getString(R.string.deleting_site),
                    "<b>" + MainActivity.tempSite.getName().toUpperCase() + "</b>")));
        }
        doYouWannaTextView.append(getResources().getString(R.string.confirm_action));

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAcceptButtonClicked(mAction);
                getDialog().dismiss();
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
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
        void onAcceptButtonClicked(String action);
    }
}