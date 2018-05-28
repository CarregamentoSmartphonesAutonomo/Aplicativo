package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.RCClient;

import java.io.DataInputStream;

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

                // Send first command to raspberry
                rcClient.sendChargeStep(666);

                DataInputStream dIn = null;

                // Receiving data response from raspberry
                boolean done = false;
                while(!done) {
                    dIn = new DataInputStream(rcClient.getSocket().getInputStream());
                    byte messageType = dIn.readByte();

                    switch(messageType)
                    {
                        case 1: // Type A
                            System.out.println("Message A: " + dIn.readUTF());
                            break;
                        case 2: // Type B
                            System.out.println("Message B: " + dIn.readUTF());
                            break;
                        case 3: // Type C
                            System.out.println("Message C [1]: " + dIn.readUTF());
                            System.out.println("Message C [2]: " + dIn.readUTF());
                            break;
                        default:
                            done = true;
                    }
                }

                dIn.close();
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
