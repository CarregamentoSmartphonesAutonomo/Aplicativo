package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.LoginActivity;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.MainActivity;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.User;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.UserDAO;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class UserRegisterFragment extends Fragment implements View.OnClickListener{

    private Button btRegister;
    private Button btnBack;
    private EditText etEmail;
    private EditText etCpf;
    private EditText etPassword;
    private EditText etName;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;
    public UserRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View view = inflater.inflate(R.layout.fragment_user_register, container, false);
        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        btRegister = view.findViewById(R.id.register_bt);
        etEmail = view.findViewById(R.id.user_email_et);
        etCpf = view.findViewById(R.id.user_cpf_et);
        etPassword = view.findViewById(R.id.user_password_et);
        etName = view.findViewById(R.id.user_name_et);
        btnBack = (Button) view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

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
        final String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        final String name = etName.getText().toString().trim();
        final String CPF = etCpf.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(CPF) || TextUtils.isEmpty(name)){
            Toast.makeText(getActivity(),"É necessário preencher todos os campos",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            final User user = new User(name,email,CPF,Integer.parseInt(password));
            //Log.d(TAG, "Queremos saber a verdade: " + name );

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");

                        //Add name and cpf
                        Map<String, Object> newContact = new HashMap<>();
                        newContact.put("name", name);
                        newContact.put("cpf", CPF);
                        newContact.put("email", email);

                        db.collection("User").document(CPF)
                                .set(newContact)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        UserDAO userDao = UserDAO.getInstance();
                                        userDao.setUser(user);
                                        Toast.makeText(getActivity(),"Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("ERROR",  e.getMessage());
                                    }
                                });


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
