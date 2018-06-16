package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.RCClient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharginProcessFragment extends Fragment {
    private final String TAG = CharginProcessFragment.class.getSimpleName();

    RCClient rcClient;
    int pulseWidth = 1400;
    private OnMessageReceived mMessageListener = null;

    Thread thread = new Thread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            Log.d("ChargingProcessFragment","Thread run -> starting connection...");
            rcClient = new RCClient();

            // Send first command to raspberry
            rcClient.sendChargeStep("1");

            Log.d(TAG, "-> Receiving data...");

            // Receiving data response from raspberry
            //boolean done = true;

            try {
                //receive the message which the server sends back
                BufferedReader in = new BufferedReader(new InputStreamReader(rcClient.getSocket().getInputStream()));

                boolean isRunning = true;

                //in this while the client listens for the messages sent by the server
                while (isRunning) {
                    String serverCommand = in.readLine();
                    Log.d(TAG, "Server command -> " + serverCommand);
                    String serverData = in.readLine();

                    switch (serverCommand){
                        case "1":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending second command -> 2");
                            rcClient.sendChargeStep("2");
                            continue;
                        case "2":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending third command -> 3");
                            rcClient.sendChargeStep("3");
                            continue;
                        case "3":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending forth command -> 4");
                            rcClient.sendChargeStep("4");
                            continue;
                        case "4":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending fith command -> 5");
                            //rcClient.sendChargeStep("5\n");
                            continue;
                        case "5":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Finishing connection");
                            //rcClient.sendChargeStep("ahauhau");
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

            /*dIn = new DataInputStream(rcClient.getSocket().getInputStream());
                    byte messageType = dIn.readByte();

                    Log.d(TAG, "Data received -> " + String.valueOf(messageType));
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
            }*/
        }
    });

    public interface OnMessageReceived {
        void messageReceived(String message);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d("ChargingProcessFragment","onCreateView -> starting thread...");
        thread.start();

        return inflater.inflate(R.layout.fragment_chargin_process, container, false);
    }
}
