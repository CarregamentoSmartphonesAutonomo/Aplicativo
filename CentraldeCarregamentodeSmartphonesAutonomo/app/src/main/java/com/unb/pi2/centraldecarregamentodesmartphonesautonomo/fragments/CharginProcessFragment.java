package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.RCClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharginProcessFragment extends Fragment {

    RCClient rcClient;
    int pulseWidth = 1400;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.d("ChargingProcessFragment","Thread run -> starting connection...");
            try  {
                rcClient = new RCClient();
                int oldPW = 0;
                while (true){
                    rcClient.turn(666);
                    break;
                }
                rcClient.closeUp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("ChargingProcessFragment","onCreateView -> starting thread...");
        thread.start();

        return inflater.inflate(R.layout.fragment_chargin_process, container, false);
    }
}
