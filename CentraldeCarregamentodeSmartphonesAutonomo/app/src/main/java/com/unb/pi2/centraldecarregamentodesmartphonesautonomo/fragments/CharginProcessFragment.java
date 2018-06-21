package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.PaymentConnection;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.RCClient;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.CreditCard;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import cn.carbs.android.library.MDDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class CharginProcessFragment extends Fragment implements Observer {
    private final String TAG = CharginProcessFragment.class.getSimpleName();

    private final int CREATE_NEW_CARD = 0;

    private LinearLayout cardContainer;

    private View view;

    private RCClient rcClient;
    private Button backCancelButton;
    private Button cabin1Button;
    private Button cabin2Button;
    private Button cabin3Button;
    private Button closeCabinButton;
    private ImageView facialRecognitionInstruction1;
    private ImageView facialRecognitionInstruction2;
    private ImageView facialRecognitionInstruction3;
    private TextView timer;

    private String firstCommand;
    private String cabinChosen;

    Thread thread = new Thread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            Log.d("ChargingProcessFragment","Thread run -> starting connection...");
            rcClient = new RCClient();

            // Send first command to raspberry
            rcClient.sendChargeStep(firstCommand+"|null");

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

                            // Slitting serverData to get the freeCabins
                            String freeCabins[] = serverData.split(",");
                            setFreeCabins(freeCabins);

                            // Verify if the buttons were clicked
                            cabin1Button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Sending data to obtain photos from facial recognition
                                    cabinChosen = "1";
                                    rcClient.sendChargeStep("2|01"/*+userDAO.getUser().getcpf()*/);
                                    showFacialRecognitionIntructions();
                                }
                            });

                            cabin2Button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Sending data to obtain photos from facial recognition
                                    cabinChosen = "2";
                                    rcClient.sendChargeStep("2|01");
                                    showFacialRecognitionIntructions();
                                }
                            });

                            cabin3Button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Sending data to obtain photos from facial recognition
                                    cabinChosen = "3";
                                    rcClient.sendChargeStep("2|01");
                                    showFacialRecognitionIntructions();
                                }
                            });
                            continue;

                        // Receiving if the facial recognition was successful (True or False)
                        case "2":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending third command -> 3");

                            if(serverData.equals("True")){
                                showConfirmButton();
                            }

                            // Seding data to opening the cab (name, cpf, cabin number)
                            rcClient.sendChargeStep("3|Dario,01," + cabinChosen);
                            continue;

                        // Receiving the cellphone insertion in the cabin was successful
                        case "3":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending forth command -> 4");

                            closeCabinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Confirm the cabin confirmation
                                    showTimer();
                                    firstCommand = "4";
                                }
                            });
                            continue;

                        // Receiving if the confirmation was successful (true or false)
                        case "4":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Sending fith command -> 5");

                            // Removing smartphone (sending cabin number)
                            rcClient.sendChargeStep("5|"+cabinChosen);
                            continue;

                        // Receiving if the removal was successful (true or false)
                        case "5":
                            Log.d(TAG, "Server data -> " + serverData);
                            Log.d(TAG, "Finishing connection");

                            payment();

                            Fragment newFragment = new MainFragment();
                            FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();

                            transaction.replace(R.id.fragment_container_fl, newFragment);
                            transaction.addToBackStack(null);

                            // Commit the transaction
                            transaction.commit();

                            // Finishing connection.
                            //rcClient.sendChargeStep("f|null");
                            //rcClient.closeUp();
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

            } /*finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                //rcClient.closeUp();
            }*/
        }
    });
    
    private void setFreeCabins(String freeCabins[]){
        // Setting visibility for buttons
        cabin1Button.setVisibility(View.VISIBLE);
        cabin2Button.setVisibility(View.VISIBLE);
        cabin3Button.setVisibility(View.VISIBLE);

        for (String freeCabin : freeCabins) {
            switch (freeCabin) {
                case "1":
                    cabin1Button.setClickable(true);
                    break;
                case "2":
                    cabin2Button.setClickable(true);
                    break;
                case "3":
                    cabin3Button.setClickable(true);
                    break;
            }
        }
    }

    private void showFacialRecognitionIntructions(){
        cabin1Button.setVisibility(View.GONE);
        cabin2Button.setVisibility(View.GONE);
        cabin3Button.setVisibility(View.GONE);

        facialRecognitionInstruction1.setVisibility(View.VISIBLE);
        facialRecognitionInstruction2.setVisibility(View.VISIBLE);
        facialRecognitionInstruction3.setVisibility(View.VISIBLE);
    }

    private void showConfirmButton(){
        facialRecognitionInstruction1.setVisibility(View.GONE);
        facialRecognitionInstruction2.setVisibility(View.GONE);
        facialRecognitionInstruction3.setVisibility(View.GONE);

        closeCabinButton.setVisibility(View.VISIBLE);
    }

    private void showTimer(){
        closeCabinButton.setVisibility(View.GONE);
        backCancelButton.setText("Terminar Carregamento");


    }

    private void payment(){
        Log.d("payment", "Entrou no método");

        Intent intent = new Intent(getContext(), CardEditActivity.class);
        intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, "PAIGE ADRIAN");
        intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, "5185055284268687");
        intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, "03/19");
        intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
        intent.putExtra(CreditCardUtils.EXTRA_CARD_CVV, "319");
        intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true);
        startActivityForResult(intent, CREATE_NEW_CARD);

    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Log.d("OnActivityResult", "Entrou no método!");

            final String name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            final String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            final String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            final String expiryMonth = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY).substring(0,2);
            final String expiryYear = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY).substring(3,5);
            final String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            Log.d("onActivityResult","Expiry Month-> " + expiryMonth);
            Log.d("onActivityResult","Expiry Year-> " + expiryYear);
            Log.d("onActivityResult", "CVV -> " + cvv);


            final CreditCardView creditCardView = new CreditCardView(getContext());

            creditCardView.setCVV(cvv);
            creditCardView.setCardHolderName(name);
            creditCardView.setCardExpiry(expiry);
            creditCardView.setCardNumber(cardNumber);

            new MDDialog.Builder(getActivity())
                    .setTitle("Pagamento")
                    .setContentView(R.layout.payment)
                    .setContentViewOperator(new MDDialog.ContentViewOperator() {
                        @Override
                        public void operate(View contentView) {
                            cardContainer = contentView.findViewById(R.id.card_container);
                            cardContainer.addView(creditCardView);
                            int index = cardContainer.getChildCount() - 1;
                            /*addCardListener(index, creditCardView);*/
                        }
                    })
                    .setNegativeButton("Cancelar", new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setPositiveButton("Realizar Pagamento", new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            CreditCard creditCard = new CreditCard(CharginProcessFragment.this );
                            creditCard.setCardNumber(cardNumber);
                            creditCard.setName(name);
                            creditCard.setMonth(expiryMonth);
                            creditCard.setYear(expiryYear);
                            creditCard.setCvv("771");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                getPaymentToken(creditCard);
                            }
                        }
                    })
                    .create().show();
        }

    }

    private void showMessage(final String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void update(Observable o, Object arg) {
        CreditCard creditCard = (CreditCard) o;

        Log.d("update", "On method.");
        if(creditCard.getToken() == null){
            showMessage(creditCard.getError());
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fierce-basin-32562.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PaymentConnection paymentConnection = retrofit.create(PaymentConnection.class);
        Call<String> call = paymentConnection.sendPayment(100, creditCard.getToken());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("onResponse", "Connected to server.");
                if(response.isSuccessful()) {
                    Log.d("onResponse", "response.body() -> " + response.body());
                    showMessage(response.body());
                }
                else{
                    try {
                        Log.d("onResponse", "response is not successful -> " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Main Fragment", t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getPaymentToken(CreditCard creditCard){
        Log.d("getPaymentToken", "on method.");
        WebView webView = view.findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/index.html");
        WebView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(creditCard, "Android");
        Log.d("getPaymentToken", "leaving method.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chargin_process, container, false);

        //userDAO = UserDAO.getUser().getCpf();

        // Instanciating view elements
        backCancelButton = view.findViewById(R.id.back_cancel_bt);
        cabin1Button = view.findViewById(R.id.cabin1_bt);
        cabin2Button = view.findViewById(R.id.cabin2_bt);
        cabin3Button = view.findViewById(R.id.cabin3_bt);
        facialRecognitionInstruction1 = view.findViewById(R.id.facial_rec_instr1_iv);
        facialRecognitionInstruction2 = view.findViewById(R.id.facial_rec_instr2_iv);
        facialRecognitionInstruction3 = view.findViewById(R.id.facial_rec_instr3_iv);
        closeCabinButton = view.findViewById(R.id.close_cabin_bt);
        timer = view.findViewById(R.id.timer_tv);

        // Setting clickable to false
        cabin1Button.setClickable(false);
        cabin2Button.setClickable(false);
        cabin3Button.setClickable(false);

        backCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cabin1Button.setVisibility(View.GONE);
                cabin2Button.setVisibility(View.GONE);
                cabin3Button.setVisibility(View.GONE);
                facialRecognitionInstruction1.setVisibility(View.GONE);
                facialRecognitionInstruction2.setVisibility(View.GONE);
                facialRecognitionInstruction3.setVisibility(View.GONE);
                closeCabinButton.setVisibility(View.GONE);

                if(backCancelButton.getText().equals("Cancelar")){
                    firstCommand = "1";
                }
                else {
                    rcClient.sendChargeStep("4|null");
                }
            }
        });

        Log.d("ChargingProcessFragment","onCreateView -> starting thread...");
        thread.start();

        return view;
    }
}
