package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.LoginActivity;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.MainActivity;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.PaymentConnection;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.RCClient;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.CreditCard;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.UserDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
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
    private UserDAO userDAO;

    private RCClient rcClient;
    private Button backCancelButton;
    private Button cabin1Button;
    private Button cabin2Button;
    private Button cabin3Button;
    private Button logOutButton;
    private Button closeCabinButton;
    private ImageView facialRecognitionInstruction1;
    private ImageView facialRecognitionInstruction2;
    private ImageView facialRecognitionInstruction3;
    private TextView instructionsText;
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
            rcClient.sendChargeStep(firstCommand+"|"+userDAO.getUser().getCabin());

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

                    if(serverCommandResponse!=null){
                        switch (serverCommandResponse){
                            // Receiving free cabin numbers
                            case "1":
                                Log.d(TAG, "Server data -> " + serverData);
                                Log.d(TAG, "Sending second command -> 2");

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        instructionsText.setText("Selecione a cabine:");
                                    }
                                });
                                // Slitting serverData to get the freeCabins
                                String freeCabins[] = serverData.split(",");
                                setFreeCabins(freeCabins);

                                continue;

                                // Receiving if the facial recognition was successful (True or False)
                            case "2":
                                Log.d(TAG, "Server data -> " + serverData);
                                Log.d(TAG, "Sending third command -> 3");

                                if(getActivity() != null){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            instructionsText.setText("Instruções:");
                                        }
                                    });
                                }

                                if(serverData.equals("True")){
                                    rcClient.sendChargeStep("3|Dario,01," + userDAO.getUser().getCabin());
                                }
                                else {
                                    rcClient.sendChargeStep("2|01");
                                }

                                continue;

                                // Receiving the cellphone insertion in the cabin was successful
                            case "3":
                                Log.d(TAG, "Server data -> " + serverData);
                                Log.d(TAG, "Sending forth command -> 4");

                                if(serverData.equals("True")){
                                    showConfirmButton();
                                }

                                continue;

                                // Receiving if the confirmation was successful (true or false)
                            case "4":
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        instructionsText.setText("Aguarde o reconhecimento facial...");
                                    }
                                });

                                Log.d(TAG, "Server data -> " + serverData);
                                Log.d(TAG, "Sending fith command -> 5");

                                continue;

                                // Receiving if the removal was successful (true or false)
                            case "5":
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        instructionsText.setText("Rosto reconhecido");
                                    }
                                });

                                Log.d(TAG, "Server data -> " + serverData);
                                Log.d(TAG, "Finishing connection");

                                if(serverData.equals("True")){
                                    payment();
                                }
                                // Finishing connection.
                                //rcClient.sendChargeStep("f|null");

                                continue;
                            case "6":
                                Log.d(TAG, "Server data -> " + serverData);
                                Log.d(TAG, "Finishing connection");

                                if(serverData.equals("True")){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            instructionsText.setText("Carregamento conluído! Retire seu celular e pressione o botão de confirmação.");
                                            backCancelButton.setText("Confirmar Retirada");
                                        }
                                    });
                                }
                                continue;
                            case "7":
                                isRunning = false;
                                rcClient.closeUp();
                                Fragment newFragment = new MainFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                transaction.replace(R.id.fragment_container_fl, newFragment);
                                transaction.addToBackStack(null);

                                // Commit the transaction
                                transaction.commit();
                            default:
                                Log.d(TAG, "Command not found.");
                                //isRunning = false;
                                break;
                        }
                    }
                    else {
                        isRunning = false;
                        rcClient.closeUp();
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

    private void setFreeCabins(final String freeCabins[]){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Setting visibility for buttons

                for (String freeCabin : freeCabins) {
                    Log.d("Freecabin", "-> " + freeCabin);
                    switch (freeCabin) {
                        case "1":
                            cabin1Button.setVisibility(View.VISIBLE);
                            cabin1Button.setClickable(true);
                            break;
                        case "2":
                            cabin2Button.setVisibility(View.VISIBLE);
                            cabin2Button.setClickable(true);
                            break;
                        case "3":
                            cabin3Button.setVisibility(View.VISIBLE);
                            cabin3Button.setClickable(true);
                            break;
                    }
                }
            }
        });
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instructionsText.setText("Rosto confirmado!\n" +
                        "Após colocar o celular na cabine, pressione o botão para fechar.");

                facialRecognitionInstruction1.setVisibility(View.GONE);
                facialRecognitionInstruction2.setVisibility(View.GONE);
                facialRecognitionInstruction3.setVisibility(View.GONE);

                closeCabinButton.setVisibility(View.VISIBLE);
            }
        });
    }


    private void showTimer(){
        closeCabinButton.setVisibility(View.GONE);
        timer.setVisibility(View.VISIBLE);
        logOutButton.setVisibility(View.VISIBLE);
        backCancelButton.setText("Terminar Carregamento");
        instructionsText.setText("Seu celular está carregando.\n" +
                "Tempo de carregamento:");

        // Start timer
        if(userDAO.getUser().getChargeTime() == 0){
            startTime = System.currentTimeMillis();
            userDAO.getUser().setChargeTime(startTime);
            updateApi();
        }
        else {
            startTime = userDAO.getUser().getChargeTime();
        }
        timerHandler.postDelayed(timerRunnable, 0);

        Log.d("showTimer","-> Exiting method");
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

    private int value;
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
                                rcClient.sendChargeStep("6|"+userDAO.getUser().getCabin());
                                instructionsText.setText("Carregamento concluído!");
                                backCancelButton.setText("Voltar");
                            }
                        }
                    })
                    .create().show();
        }

    }

    private void showMessage(final String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void updateApi(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user = db.collection("User").document(userDAO.getUser().getCpf());
        user.update("chargeTime", userDAO.getUser().getChargeTime())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"User update chargeTime -> Success!");
                    }
                });
        user.update("cabin", userDAO.getUser().getCabin());
        Map<String,Object> newCharge = new HashMap<>();
        newCharge.put("charge", totalTime);

        user.collection("ChargeHistory").document(String.valueOf(startTime))
                .set(newCharge)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"User update chargeHistory -> Success!");
                    }
                });
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

        // The minimum payment is 100 Real cents
        if(value < 100){
            value = 100;
        }
        Call<String> call = paymentConnection.sendPayment(value, creditCard.getToken());
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

    //runs without a timer by reposting this handler at the end of the runnable
    private Handler timerHandler = new Handler();
    private long startTime;
    private int totalTime;
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            value = (int) (seconds * 0.09);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;

            totalTime = minutes;

            timer.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        /*timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.button);
        b.setText("start");*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chargin_process, container, false);

        userDAO = UserDAO.getInstance();
        cabinChosen = userDAO.getUser().getCabin();

        // Instanciating view elements
        backCancelButton = view.findViewById(R.id.back_cancel_bt);
        cabin1Button = view.findViewById(R.id.cabin1_bt);
        cabin2Button = view.findViewById(R.id.cabin2_bt);
        cabin3Button = view.findViewById(R.id.cabin3_bt);
        facialRecognitionInstruction1 = view.findViewById(R.id.facial_rec_instr1_iv);
        facialRecognitionInstruction2 = view.findViewById(R.id.facial_rec_instr2_iv);
        facialRecognitionInstruction3 = view.findViewById(R.id.facial_rec_instr3_iv);
        closeCabinButton = view.findViewById(R.id.close_cabin_bt);
        instructionsText = view.findViewById(R.id.instructions_tv);
        timer = view.findViewById(R.id.timer_tv);
        logOutButton = view.findViewById(R.id.logout_bt);

        if(userDAO.getUser().getChargeTime() == 0){
            firstCommand = "1";
        }
        else {
            firstCommand = "4";
            showTimer();
        }

        // Setting clickable to false
        cabin1Button.setClickable(false);
        cabin2Button.setClickable(false);
        cabin3Button.setClickable(false);

        //timerHandler.postDelayed(timerRunnable, 0);

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
                logOutButton.setVisibility(View.GONE);

                userDAO.getUser().setChargeTime(0);
                updateApi();

                if(backCancelButton.getText().equals("Cancelar") || backCancelButton.getText().equals("Voltar")){
                    firstCommand = "1";

                    Fragment newFragment = new MainFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_container_fl, newFragment);
                    transaction.addToBackStack(null);

                    // Commit the transaction
                    transaction.commit();
                    rcClient.sendChargeStep("5|"+cabinChosen);
                }
                else if(backCancelButton.getText().equals("Terminar Carregamento")) {
                    // Stop timer
                    timerHandler.removeCallbacks(timerRunnable);
                    // Send command to rasp to stop charging and open de cabin.
                    rcClient.sendChargeStep("5|"+cabinChosen);
                }
                else if (backCancelButton.getText().equals("Confirmar Retirada")){
                    rcClient.sendChargeStep("7|"+userDAO.getUser().getCabin());
                }
            }
        });

        // Verify if the buttons were clicked
        cabin1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to obtain photos from facial recognition
                cabinChosen = "1";
                userDAO.getUser().setCabin(cabinChosen);
                updateApi();
                rcClient.sendChargeStep("2|01"/*+userDAO.getUser().getcpf()*/);
                instructionsText.setText("Obtendo imagens para reconhecimento facial, aguarde...");
                showFacialRecognitionIntructions();
            }
        });

        cabin2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to obtain photos from facial recognition
                cabinChosen = "2";
                userDAO.getUser().setCabin(cabinChosen);
                updateApi();
                rcClient.sendChargeStep("2|01");
                instructionsText.setText("Obtendo imagens para reconhecimento facial, aguarde...");
                showFacialRecognitionIntructions();
            }
        });

        cabin3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending data to obtain photos from facial recognition
                cabinChosen = "3";
                userDAO.getUser().setCabin(cabinChosen);
                updateApi();
                rcClient.sendChargeStep("2|01");
                instructionsText.setText("Obtendo imagens para reconhecimento facial, aguarde...");
                showFacialRecognitionIntructions();
            }
        });

        closeCabinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirm the cabin confirmation
                rcClient.sendChargeStep("4|"+userDAO.getUser().getCabin());
                showTimer();
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcClient.closeUp();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });


        Log.d("ChargingProcessFragment","onCreateView -> starting thread...");
        thread.start();

        return view;
    }
}
