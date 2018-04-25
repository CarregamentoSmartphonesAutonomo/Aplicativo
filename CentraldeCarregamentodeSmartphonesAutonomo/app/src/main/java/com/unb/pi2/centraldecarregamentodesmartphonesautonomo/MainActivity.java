package com.unb.pi2.centraldecarregamentodesmartphonesautonomo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editName, editCpf, editEmail, editPassword;
    ListView listDados;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    User userSelect;

    private List<User> listUser = new ArrayList<User>();
    private ArrayAdapter<User> arrayAdapterUser;

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
        eventDatabase();

        listDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                userSelect = (User)adapterView.getItemAtPosition(position);
                editName.setText(userSelect.getName());
                editEmail.setText(userSelect.getEmail());
                editCpf.setText(userSelect.getCpf());
                editPassword.setText(userSelect.getPassword());
            }
        });
    }

    private void initFirebase(){

        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void eventDatabase(){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listUser.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    User user = objSnapshot.getValue(User.class);
                    listUser.add(user);
                }
                arrayAdapterUser = new ArrayAdapter<User>(MainActivity.this, android.R.layout.simple_list_item_1, listUser);
                listDados.setAdapter(arrayAdapterUser);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
