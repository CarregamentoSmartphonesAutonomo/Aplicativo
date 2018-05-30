package com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.LoginActivity;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private EditText oldEmail, newEmail, password, newPassword;
    private Button btnEditUser, btnRemoveUser;
    private FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_edit, container, false);

        FirebaseAuth auth;

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        newEmail = (EditText) view.findViewById(R.id.new_email);
        newPassword = (EditText) view.findViewById(R.id.new_Password);
        btnEditUser = (Button) view.findViewById(R.id.sending_edit_button);
        btnRemoveUser = (Button) view.findViewById(R.id.remove_button);

        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Senha muito curta, entre com no mínimo 6 caracteres");
                    } else {
                        updateUser();
                    }
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return view;

    }

    // ------------ Created Methods ------------
    private void updateUser(){
        String email = newEmail.getText().toString().trim();
        String password = newPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getActivity(),"É necessário preencher todos os campos",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            user.updateEmail(newEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Email atualizado com sucesso!", Toast.LENGTH_LONG).show();
                                //signOut();
                            } else {
                                Toast.makeText(getActivity(), "Falha ao atualizar o email!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            user.updatePassword(newPassword.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                                //signOut();
                            } else {
                                Toast.makeText(getActivity(), "Falha ao atualizar a senha!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


}
