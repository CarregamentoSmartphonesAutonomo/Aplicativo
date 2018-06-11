package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.google.gson.Gson;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.Utils.PaymentConnection;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.CreditCard;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import cn.carbs.android.library.MDDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class    MainFragment extends Fragment implements Observer {

    private final int CREATE_NEW_CARD = 0;

    private LinearLayout cardContainer;
    private View view;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        Button paymentButton = view.findViewById(R.id.payment_bt);
        Button charge = view.findViewById(R.id.rasp_connection_bt);
        charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                payment(v);
                /*// Create new fragment and transaction
                Fragment newFragment = new CharginProcessFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container_fl, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();*/
            }
        });
        /*paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment(v);
            }
        });*/

        return view;
    }

    // ------------ Created Methods ------------
    public void payment(View v){
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

    private void addCardListener(final int index, CreditCardView creditCardView) {
        creditCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreditCardView creditCardView = (CreditCardView) v;
                String cardNumber = creditCardView.getCardNumber();
                String expiry = creditCardView.getExpiry();
                String cardHolderName = creditCardView.getCardHolderName();
                String cvv = creditCardView.getCVV();

                /*Intent intent = new Intent(getContext(), CardEditActivity.class);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, cardHolderName);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER, cardNumber);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY, expiry);
                intent.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
                intent.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, false);

                // start at the CVV activity to edit it as it is not being passed
                intent.putExtra(CreditCardUtils.EXTRA_ENTRY_START_PAGE, CreditCardUtils.CARD_CVV_PAGE);
                startActivityForResult(intent, index);*/
            }
        });
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
                            addCardListener(index, creditCardView);
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
                            CreditCard creditCard = new CreditCard(MainFragment.this );
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

    private void sendPaymentData(CreditCard creditCard){

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
}
