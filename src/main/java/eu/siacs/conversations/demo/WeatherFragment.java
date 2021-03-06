package eu.siacs.conversations.demo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;

import eu.siacs.conversations.R;
import eu.siacs.conversations.demo.adapter.CityListAdapter;
import eu.siacs.conversations.demo.bean.ResWeather;


public class WeatherFragment extends Fragment {

    Spinner citySpinner;
    TextView txtTemp, txtHum, txtWind, txtCityName, txtHighTemp, txt_LowTemp;
    RadioGroup rdGrpCity;

    private CityListAdapter cityListAdapter;
    private WeatherViewModel weatherViewModel;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        citySpinner = view.findViewById(R.id.sp_city_list);
        txtTemp = view.findViewById(R.id.txt_temp);
        txtHum = view.findViewById(R.id.txt_hum);
        txtWind = view.findViewById(R.id.txt_wind);
        txtCityName = view.findViewById(R.id.txt_city_name);
        txtHighTemp = view.findViewById(R.id.txt_high_temp);
        txt_LowTemp = view.findViewById(R.id.txt_low_temp);
        rdGrpCity = view.findViewById(R.id.rd_grp_city);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cityName = (String) cityListAdapter.getItem(position);

//                weatherViewModel.getWeatherDetails(cityName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rdGrpCity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkButton = group.findViewById(checkedId);
                String cityName = checkButton.getText().toString();
                weatherViewModel.getWeatherDetails(cityName);
            }
        });

        rdGrpCity.check(R.id.rd_btn_delhi);

//        cityListAdapter = new CityListAdapter(this.getContext(), new ArrayList<>());
//        citySpinner.setAdapter(cityListAdapter);
//
//        cityListAdapter.setValues(getListOfCities());

    }

    private void initView() {
        weatherViewModel = ViewModelProviders.of(getActivity()).get(WeatherViewModel.class);

        weatherViewModel.getLiveData().observe(this.getViewLifecycleOwner(), new Observer<ResWeather>() {
            @Override
            public void onChanged(ResWeather resWeather) {
                Log.e("OBSERVER","onChanged");
                if (resWeather != null) {
                    txtTemp.setText(String.valueOf(resWeather.getMain().getTemp().intValue())+"°C");
                    txtHum.setText(String.valueOf(resWeather.getMain().getHumidity().intValue())+"%");
                    txtWind.setText(String.valueOf(resWeather.getMain().getPressure().intValue())+"Hpa");
                    txtHighTemp.setText("High: "+String.valueOf(resWeather.getMain().getTempMax().intValue())+"°C");
                    txt_LowTemp.setText("Low: "+String.valueOf(resWeather.getMain().getTempMin().intValue())+"°C");
                    txtCityName.setText(String.valueOf(resWeather.getName()));

                } else {
                    Log.e("OBSERVER","resWeather is null");
                }
            }
        });

    }

    private ArrayList<String> getListOfCities() {
        ArrayList<String> listOfItems = new ArrayList<>();

        listOfItems.add("Delhi");
        listOfItems.add("Bangalore");
        listOfItems.add("Kolkata");
        listOfItems.add("Chennai");
        listOfItems.add("Mumbai");

        return listOfItems;
    }

    public void onWeatherDataGet(ResWeather response){

        txtTemp.setText("Temparature: "+String.valueOf(response.getMain().getTemp()));
        txtHum.setText("Humidity: "+String.valueOf(response.getMain().getHumidity()));
        txtWind.setText("WindSpeed: "+String.valueOf(response.getWind().getSpeed()));

    }
}