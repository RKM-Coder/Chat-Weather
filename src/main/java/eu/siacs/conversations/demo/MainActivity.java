package eu.siacs.conversations.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toast;

import eu.siacs.conversations.R;
import eu.siacs.conversations.demo.apiservice.APIError;
import eu.siacs.conversations.demo.apiservice.NetworkService;


public class MainActivity extends AppCompatActivity {

    WeatherFragment weatherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        weatherFragment = new WeatherFragment();

        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.fragment, weatherFragment).addToBackStack(null).commit();

    }


}