package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.MainActivity;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;

import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText etEmail;
    private EditText etPassword;
    private Button btLogin;
    private TextView tvUserRegister;
    private TextView tvSendEmail;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


        etEmail = view.findViewById(R.id.login_email_et);
        etPassword = view.findViewById(R.id.login_password_et);
        btLogin = view.findViewById(R.id.login_bt);
        tvUserRegister = view.findViewById(R.id.user_register_tv);
        tvSendEmail = view.findViewById(R.id.send_email);

        progressDialog = new ProgressDialog(getActivity());

        btLogin.setOnClickListener(this);
        tvUserRegister.setOnClickListener(this);
        tvSendEmail.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btLogin){
            login();
        }
        else if (view == tvUserRegister){
            UserRegisterFragment userRegisterFragment= new UserRegisterFragment();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_fl, userRegisterFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
        else if (view == tvSendEmail){
            ResetPasswordActivity sendEmailUser = new ResetPasswordActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_fl, sendEmailUser)
                        .addToBackStack(null)
                        .commit();
            }

        }
    }

    // ------------ Created Methods ------------
    private void login(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getActivity(),"É necessário preencher todos os campos",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Validando");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    // Start Main Activity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
    }
}