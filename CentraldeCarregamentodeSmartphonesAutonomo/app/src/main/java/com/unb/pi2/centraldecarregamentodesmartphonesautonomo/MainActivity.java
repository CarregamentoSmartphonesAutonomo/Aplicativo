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

public class MainActivity extends AppCompatActivity {

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
    }
}
