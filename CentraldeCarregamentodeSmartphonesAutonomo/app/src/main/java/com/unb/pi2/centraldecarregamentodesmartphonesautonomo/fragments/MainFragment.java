package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.CreditCard;

import cn.carbs.android.library.MDDialog;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

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
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment(v);
            }
        });

        return view;
    }

    // ------------ Created Methods ------------
    public void payment(View v){
        Log.d("payment", "Entrou no método");

        Intent intent = new Intent(getContext(), CardEditActivity.class);
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

            /*Log.d("onActivityResult","Expiry Month-> " + expiryMonth);
            Log.d("onActivityResult","Expiry Year-> " + expiryYear);*/


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
                            CreditCard creditCard = new CreditCard();
                            creditCard.setCardNumber(cardNumber);
                            creditCard.setName(name);
                            creditCard.setMonth(expiryMonth);
                            creditCard.setYear(expiryYear);
                            creditCard.setCvv(cvv);

                            getPaymentToken(creditCard);
                        }
                    })
                    .create().show();
        }

    }

    private void getPaymentToken(CreditCard creditCard){
        WebView webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(creditCard, "Android");
        webView.loadUrl("");
    }
}
