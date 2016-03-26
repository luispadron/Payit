package lpadron.me.project1_payit.controllers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lpadron.me.project1_payit.R;

public class AboutFragment extends Fragment {

    @Bind(R.id.testButton) Button testButton;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Bind the views
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.testButton)
    public void testOnClick(View v) {
        Toast.makeText(getActivity(), "TEST", Toast.LENGTH_SHORT).show();
    }

}
