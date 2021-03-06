package eu.siacs.conversations.demo.apiservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import eu.siacs.conversations.demo.AppConstants;
import eu.siacs.conversations.demo.bean.ResWeather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Murugeshwaran M on 05-03-2021.
 */
public class NetworkService {

    private Context mContext;

    public NetworkService() {
    }

    public void init(Context mContext){
        this.mContext = mContext;
    }

    public interface NetworkServiceListener<T> {
        void onSuccess(T response);

        void onFail(T response);

        void onAuthFail(T response);
    }

    public boolean haveNetworkAccess() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo connection = manager.getActiveNetworkInfo();
            if (connection != null && connection.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public void getWeather(String cityName, NetworkServiceListener listener){
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<ResWeather> call = api.getWeather(cityName, "metric", AppConstants.API_KEY);

        call.enqueue(new Callback<ResWeather>() {
            @Override
            public void onResponse(Call<ResWeather> call, Response<ResWeather> response) {
                if (response.code() == 200){
                    if (listener != null)
                        listener.onSuccess(response.body());
                } else if (response.code() == 401){
                    Log.e("NETWORK","UnAthorised!!"+response.code());
                    if (listener != null)
                        listener.onAuthFail(makeErrorResponse("UnAuthorized!!!", response.code()));
                } else {
                    if (listener != null)
                        listener.onFail(makeErrorResponse("Failed to get weather", response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResWeather> call, Throwable t) {
                t.printStackTrace();
                if (listener != null)
                    listener.onFail(makeErrorResponse("Failed to get weather", 500));
            }
        });

    }

    public void getWeather(String cityName, MutableLiveData<ResWeather> resWeather){
        ApiInterface api = ApiClient.getClient().create(ApiInterface.class);

        Call<ResWeather> call = api.getWeather(cityName, "metric", AppConstants.API_KEY);

        call.enqueue(new Callback<ResWeather>() {
            @Override
            public void onResponse(Call<ResWeather> call, Response<ResWeather> response) {
                if (response.code() == 200){
                    if (resWeather != null)
                        resWeather.postValue(response.body());
                    else
                        Log.e("API","resWeather is null");
                } else if (response.code() == 401){

                } else {

                }
            }

            @Override
            public void onFailure(Call<ResWeather> call, Throwable t) {

            }
        });

    }

    private APIError makeErrorResponse(String error, int code) {
        APIError apiError = new APIError();
        apiError.setMessage(error);
        apiError.setStatusCode(code);

        return apiError;
    }

}
