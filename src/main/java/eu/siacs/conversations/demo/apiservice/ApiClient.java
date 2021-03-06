package eu.siacs.conversations.demo.apiservice;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Murugeshwaran M on 05-03-2021.
 */
public class ApiClient {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";    //prod

    private static Retrofit retrofit = null;
    private static Retrofit retrofitImage = null;

    /**
     * This method returns retrofit client instance
     *
     * @return Retrofit object
     */
    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(80, TimeUnit.SECONDS)
                .writeTimeout(80, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    //.client(getNewHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
