package com.unb.pi2.centraldecarregamentodesmartphonesautonomo;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments.EditFragment;
import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments.MainFragment;

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

    private Fragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            toolbar.setElevation(4.f);
        }
        toolbar.setLogo(R.drawable.googleg_standard_color_18);

        // Create a new Fragment to be placed in the activity layout
        MainFragment firstFragment = new MainFragment();
        newFragment = firstFragment;

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_fl, firstFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.it_edit:
                changeFragment("editFragment");
                break;

            case R.id.it_logout:
                changeFragment("logout");
                break;
        }
        return true;
    }

    // ------------ Created Methods ------------

    // Make the transition between fragments
    public void changeFragment(String fragment) {

        Bundle args = new Bundle();

        newFragment = null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case "editFragment":
                newFragment = new EditFragment();
                break;

            case "logout":
                break;
        }

        newFragment.setArguments(args);
        transaction.replace(R.id.fragment_container_fl, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

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
