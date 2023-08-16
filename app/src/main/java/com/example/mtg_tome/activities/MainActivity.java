package com.example.mtg_tome.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mtg_tome.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    TextView homeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.homeFragment, R.id.searchOptionFragment, R.id.decksFragment, R.id.collectionFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.fragmentContainerView).navigateUp()
                || super.onSupportNavigateUp();
    }
}