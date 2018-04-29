package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;

import cn.carbs.android.library.MDDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // ------------ Created Methods ------------
    public void payment(View view){
        new MDDialog.Builder(getActivity())
                .setTitle("Pagamento")
                .setContentView(R.layout.payment)
                .setNegativeButton("Cancelar", new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("Cancelar", new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                    }
                })
                .create().show();
    }

}
