package eu.siacs.conversations.demo.apiservice;



import eu.siacs.conversations.demo.bean.ResWeather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Murugeshwaran M on 05-03-2021.
 */
public interface ApiInterface {

    @GET("weather")
    public Call<ResWeather> getWeather(
            @Query("q") String cityName,
            @Query("units") String units,
            @Query("appid") String appId
    );

}
