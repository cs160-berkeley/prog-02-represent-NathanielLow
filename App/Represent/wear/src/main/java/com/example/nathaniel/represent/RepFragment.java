package com.example.nathaniel.represent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Nathaniel on 3/1/2016.
 * Special thanks to Jack Thakar
 */
public class RepFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;
    private String mParam6;

    public RepFragment() {

    }

    public static RepFragment newInstance(String param1, String param2, String param3, String param4, String param5, String param6) {
        RepFragment fragment = new RepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getString(ARG_PARAM6);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.fragment_rep, container, false);

        TextView role = (TextView) v.findViewById(R.id.fragment_role);
        final TextView name = (TextView) v.findViewById(R.id.fragment_name);
        TextView party = (TextView) v.findViewById(R.id.fragment_party);
        TextView party2 = (TextView) v.findViewById(R.id.fragment_party2);
        TextView directions = (TextView) v.findViewById(R.id.fragment_directions);

        role.setText(mParam1);
        name.setText(mParam2);
        party.setText(mParam3);
        party2.setText(mParam4);
        directions.setText(mParam6);

        if (mParam1.equals("2012 Presidential Vote")) {
            v.setBackgroundColor(getResources().getColor(R.color.colorButton));
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mParam1.equals("2012 Presidential Vote"))) {
                    Context context = v.getContext();
                    Intent toPhoneIntent = new Intent(context, RepWatchToPhoneService.class);
                    toPhoneIntent.putExtra("REP_NAME", mParam5);
                    toPhoneIntent.putExtra("ZIP_CODE", "Blah");
                    context.startService(toPhoneIntent);
                }
            }
        });
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
