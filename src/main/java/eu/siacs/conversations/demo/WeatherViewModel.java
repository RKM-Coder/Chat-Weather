package eu.siacs.conversations.demo;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import eu.siacs.conversations.demo.apiservice.APIError;
import eu.siacs.conversations.demo.apiservice.NetworkService;
import eu.siacs.conversations.demo.bean.ResWeather;


/**
 * Created by Murugeshwaran M on 05-03-2021.
 */
public class WeatherViewModel extends AndroidViewModel implements NetworkService.NetworkServiceListener {

    MutableLiveData<ResWeather> weatherResponse;
    NetworkService networkService;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        networkService = new NetworkService();
        networkService.init(application.getApplicationContext());
        weatherResponse = new MutableLiveData<>();
    }

    public void getWeatherDetails(String cityName) {

        if (networkService.haveNetworkAccess())
            networkService.getWeather(cityName, this);

    }

    public LiveData<ResWeather> getLiveData(){
        return weatherResponse;
    }

    @Override
    public void onSuccess(Object response) {
        if (response instanceof ResWeather){
            if (weatherResponse.hasActiveObservers()){
                weatherResponse.postValue((ResWeather) response);
            } else {
                Log.e("VIEWMODEL","weatherResponse don't have active Observers");
            }
        }
    }

    @Override
    public void onFail(Object response) {
        if (response instanceof APIError)
            Log.e("VIEWMODEL","OnFailed to get weather"+((APIError) response).message());
    }

    @Override
    public void onAuthFail(Object response) {
        if (response instanceof APIError)
            Log.e("VIEWMODEL","OnAuthFailed to get weather"+((APIError) response).message());
    }
}
