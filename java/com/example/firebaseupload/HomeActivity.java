package com.example.firebaseupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.firebaseupload.Fragment.HomeFragment;
import com.example.firebaseupload.Fragment.ProfileFragment;
import com.example.firebaseupload.Fragment.SearchFragment;

import com.example.firebaseupload.NewsAndRadio.BilkentNewsActivity;
import com.example.firebaseupload.NewsAndRadio.RadioActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherId");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileId", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }



    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.ic_house:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.ic_profile:
                            SharedPreferences.Editor editor = getSharedPreferences("PREPS",MODE_PRIVATE).edit();
                            editor.putString("profileId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment=new ProfileFragment();
                            break;

                        case R.id.ic_news:
                            selectedFragment=null;
                            startActivity(new Intent(HomeActivity.this, BilkentNewsActivity.class));
                            break;

                        case R.id.ic_radio:
                            selectedFragment=null;
                            //startActivity(new Intent(HomeActivity.this, PostActivity.class));
                            break;

                        case R.id.ic_people:
                            selectedFragment = new SearchFragment();

                            break;

                    }
                    if (selectedFragment!= null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).addToBackStack(null).commit();
                    }


                    return true;
                }
            };
}
