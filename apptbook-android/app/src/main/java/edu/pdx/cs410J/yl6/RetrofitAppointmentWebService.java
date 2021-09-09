package edu.pdx.cs410J.yl6;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAppointmentWebService {

    private static volatile RetrofitAppointmentWebService INSTANCE;

    private final AppointmentRestApi appointmentRestApi;

    private RetrofitAppointmentWebService() {
        Gson gson = new GsonBuilder().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/apptbook/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        appointmentRestApi = retrofit.create(AppointmentRestApi.class);
    }

    public static RetrofitAppointmentWebService getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitAppointmentWebService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitAppointmentWebService();
                }
            }
        }
        return INSTANCE;
    }

    public AppointmentRestApi getAppointmentRestApi() {
        return appointmentRestApi;
    }

}
