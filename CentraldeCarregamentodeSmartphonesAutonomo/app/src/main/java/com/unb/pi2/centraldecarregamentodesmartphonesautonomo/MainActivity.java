package com.unb.pi2.centraldecarregamentodesmartphonesautonomo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.unb.pi2.centraldecarregamentodesmartphonesautonomo.fragments.UserRegisterFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container_fl) != null) {

            // If we're being restored from a previous state, then we don't need to do anything
            // and should return or else we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            UserRegisterFragment firstFragment = new UserRegisterFragment();
            newFragment = firstFragment;

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_fl, firstFragment).commit();

            //LinearLayout llMainMenu = (LinearLayout) findViewById(R.id.activity_main_cl);
        }

    }

    // ------------ Created Methods ------------

    // Make the transition between fragments
    public void changeFragment(View view) {

        Bundle args = new Bundle();

        newFragment = null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        newFragment.setArguments(args);
        transaction.replace(R.id.fragment_container_fl, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
