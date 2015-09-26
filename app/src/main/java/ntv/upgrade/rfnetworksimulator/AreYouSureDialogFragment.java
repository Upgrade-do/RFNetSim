package ntv.upgrade.rfnetworksimulator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Paulino on 9/20/2015.
 */
public class AreYouSureDialogFragment extends DialogFragment {

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
        title.setTextColor(getResources().getColor(R.color.colorPrimary));

        TextView doYouWannaTextView = (TextView) rootView.findViewById(R.id.doYouWannaTextView);
        ImageButton acceptButton = (ImageButton) rootView.findViewById(R.id.acceptButton);
        ImageButton declineButton = (ImageButton) rootView.findViewById(R.id.declineButton);

        if (mAction.equals("create")) {
            getDialog().setTitle("Create new Site");
            doYouWannaTextView.setText("Do you want to create a new site here ?");
        } else {
            getDialog().setTitle("Delete Site");
            doYouWannaTextView.setText("Are you sure you want to delete this site ?");
        }

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