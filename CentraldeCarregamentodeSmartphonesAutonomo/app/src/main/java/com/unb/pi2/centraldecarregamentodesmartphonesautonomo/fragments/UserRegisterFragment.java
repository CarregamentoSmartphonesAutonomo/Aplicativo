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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.User;

import static android.content.ContentValues.TAG;


public class UserRegisterFragment extends Fragment implements View.OnClickListener{

    private Button btRegister;
    private EditText etEmail;
    private EditText etCpf;
    private EditText etPassword;

    private FirebaseAuth firebaseAuth;
    public UserRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_user_register, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        btRegister = view.findViewById(R.id.register_bt);
        etEmail = view.findViewById(R.id.user_email_et);
        etCpf = view.findViewById(R.id.user_cpf_et);
        etPassword = view.findViewById(R.id.user_password_et);

        btRegister.setOnClickListener(this);

        return view;
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

    // ------------ Created Methods ------------
    private void registerUser(){
        String email = etEmail.getText().toString().trim();
        String CPF = etCpf.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(CPF)){
            Toast.makeText(getActivity(),"É necessário preencher todos os campos",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            User user = new User("Fulanão",email,Integer.parseInt(CPF),Integer.parseInt(password));
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
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
