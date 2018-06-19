package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.RCClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CharginProcessFragment extends Fragment {
    private final String TAG = CharginProcessFragment.class.getSimpleName();

    private RCClient rcClient;
    private Button cabin1Button;
    private Button cabin2Button;
    private Button cabin3Button;

    Thread thread = new Thread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            Log.d("ChargingProcessFragment","Thread run -> starting connection...");
            rcClient = new RCClient();

            // Send first command to raspberry
            rcClient.sendChargeStep("1|null");

            Log.d(TAG, "-> Receiving data...");

            // Receiving data response from raspberry
            //boolean done = true;

            try {
                //receive the message which the server sends back
                BufferedReader in = new BufferedReader(new InputStreamReader(rcClient.getSocket().getInputStream()));

                boolean isRunning = true;

                //in this while the client listens for the messages sent by the server
                while (isRunning) {
                    String serverCommandResponse = in.readLine();
                    Log.d(TAG, "Server command -> " + serverCommandResponse);
                    String serverData = in.readLine();

                    switch (serverCommandResponse){
                        // Receiving free cabin numbers
                        case "1":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending second command -> 2");

                            /*// Separar a string para verificar quais cabines estão livres
                            ArrayList<Integer> freeCabins = new ArrayList<>();
                            setFreeCabins(freeCabins);*/

                            rcClient.sendChargeStep("2|01");


                            continue;

                        // Receiving if the facial recognition was successful (True or False)
                        case "2":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending third command -> 3");

                            // Seding data to opening the cab (name, cpf, cabin number)
                            rcClient.sendChargeStep("3|Dario,01,2");
                            continue;

                        // Receiving the cellphone insertion in the cabin was successful
                        case "3":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending forth command -> 4");

                            // Confirm the cabin confirmation
                            rcClient.sendChargeStep("4|null");
                            continue;

                        // Receiving if the confirmation was successful (true or false)
                        case "4":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending fith command -> 5");

                            // Removing smartphone (sending cabin number)
                            rcClient.sendChargeStep("5|2");
                            continue;

                        // Receiving if the removal was successful (true or false)
                        case "5":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Finishing connection");

                            // Finishing connection.
                            rcClient.sendChargeStep("f|null");
                            rcClient.closeUp();
                            break;
                        default:
                            Log.d(TAG, "Command not found.");
                            isRunning = false;
                            break;
                    }
                }
            }
            catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                rcClient.closeUp();
            }
        }
    });
    
    private void setFreeCabins(ArrayList<Integer> freeCabins){
        // Verify if the buttons were clicked
        cabin1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to obtain photos from facial recognition
                rcClient.sendChargeStep("1|01");
            }
        });

        cabin2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to obtain photos from facial recognition
                rcClient.sendChargeStep("2|01");
            }
        });

        cabin3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to obtain photos from facial recognition
                rcClient.sendChargeStep("3|01");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chargin_process, container, false);

        // Instanciating view elements
        cabin1Button = view.findViewById(R.id.cabin1_bt);
        cabin2Button = view.findViewById(R.id.cabin2_bt);
        cabin3Button = view.findViewById(R.id.cabin3_bt);



        Log.d("ChargingProcessFragment","onCreateView -> starting thread...");
        thread.start();

        return view;
    }
}
