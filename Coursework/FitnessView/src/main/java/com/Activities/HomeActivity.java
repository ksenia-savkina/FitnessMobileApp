package com.Activities;

import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.container, new ImageFragment()).addToBackStack(null).commit();

        BottomNavigationView bottomNavigationView;

        bottomNavigationView = findViewById(R.id.navigation);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    Bundle args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, getIntent().getExtras().getInt(AuthorizationFragment.ID));
                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.setArguments(args);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container, profileFragment).addToBackStack(null).commit();
                    return true;
                case R.id.navigation_classes:
                    args = new Bundle();
                    args.putInt(AuthorizationFragment.ID, getIntent().getExtras().getInt(AuthorizationFragment.ID));
                    ClassesFragment classesFragment = new ClassesFragment();
                    classesFragment.setArguments(args);
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.container, classesFragment).addToBackStack(null).commit();
                    return true;
            }
            return false;
        });
    }
}