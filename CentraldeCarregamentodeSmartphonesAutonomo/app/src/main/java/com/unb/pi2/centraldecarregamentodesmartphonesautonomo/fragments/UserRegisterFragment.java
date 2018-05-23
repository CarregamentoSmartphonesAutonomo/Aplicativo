package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.User;

import static android.content.ContentValues.TAG;


public class UserRegisterFragment extends Fragment implements View.OnClickListener{

    private Button btRegister;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;

    private FirebaseAuth firebaseAuth;
    public UserRegisterFragment() {
        // Required empty public constructor
    }

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_register, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        btRegister = view.findViewById(R.id.register_bt);
        etName = view.findViewById(R.id.user_name_et);
        etEmail = view.findViewById(R.id.user_email_et);
        etPassword = view.findViewById(R.id.user_password_et);

        btRegister.setOnClickListener(this);


        return view;
    }

    private void initFirebase(){
        FirebaseApp.initializeApp(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        if(view == btRegister){
            registerUser();
        }
    }

    private void limparCampos(){
        etName.setText("");
        etEmail.setText("");
    }

    // ------------ Created Methods ------------
    private void registerUser(){
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)){
            Toast.makeText(getActivity(),"É necessário preencher todos os campos",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            final User user = new User(name,email,Integer.parseInt(password));
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        user.setName(etName.getText().toString());
                        user.setEmail(etEmail.getText().toString());
                        Log.d(TAG, "Passou em set Name e Email");

                        // Sign in success, update UI with the signed-in user's information
                        databaseReference.child("User").child(user.getEmail()).setValue(user);
                        Log.d(TAG, "Passou em databaseReference");
                        initFirebase();
                        Log.d(TAG, "Passou em initFirebase");
                        limparCampos();
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(getActivity(),"Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(),"Cadastro não realizado, por favor tente novamente...",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
