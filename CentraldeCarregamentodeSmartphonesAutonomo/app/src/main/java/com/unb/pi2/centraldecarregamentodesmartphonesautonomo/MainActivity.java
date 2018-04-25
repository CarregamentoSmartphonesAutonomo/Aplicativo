package com.unb.pi2.centraldecarregamentodesmartphonesautonomo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText editName, editCpf, editEmail, editPassword;
    ListView listDados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editName = (EditText)findViewById(R.id.user_name_et);
        editCpf = (EditText)findViewById(R.id.user_cpf_et);
        editEmail = (EditText)findViewById(R.id.user_email_et);
        editPassword = (EditText)findViewById(R.id.user_password_et);
        listDados = (ListView)findViewById(R.id.listDados);

        initFirebase();
    }

    private void initFirebase(){

        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public boolean onCreateOptionsMenu(Menu menu){
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.novo){
            User newUser = new User();
            newUser.setName(editName.getText().toString());
            newUser.setCpf(editCpf.getText().toString());
            newUser.setEmail(editEmail.getText().toString());
            newUser.setPassword(editPassword.getText().toString());
            databaseReference.child("User").child(newUser.getCpf()).setValue(newUser);
            cleanFields();
        }
    return true;

    }

    private void cleanFields(){

        editName.setText("");
        editCpf.setText("");
        editEmail.setText("");
        editPassword.setText("");
    }
}
