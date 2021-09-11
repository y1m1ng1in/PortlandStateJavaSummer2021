package edu.pdx.cs410J.yl6;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAppointmentWebService {

    private static volatile RetrofitAppointmentWebService INSTANCE;

    private final AppointmentRestApi appointmentRestApi;
    private AppointmentRestApi authenticatedAppointmentRestApi;

    private RetrofitAppointmentWebService() {
        appointmentRestApi = buildAppointmentRestApi(null);
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

    public AppointmentRestApi getAuthenticatedAppointmentRestApi() {
        return authenticatedAppointmentRestApi;
    }

    public void setAuthenticationHeader(String token) {
        authenticatedAppointmentRestApi = buildAppointmentRestApi(chain -> {
            Request request = chain.request();
            Request newRequest = request.newBuilder().header("Cookie", token).build();
            return chain.proceed(newRequest);
        });
    }

    private AppointmentRestApi buildAppointmentRestApi(Interceptor interceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);
        if (interceptor != null) {
            client.addInterceptor(interceptor);
        }
        OkHttpClient okHttpClient = client.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/apptbook/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofit.create(AppointmentRestApi.class);
    }

    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            DateFormat df = new SimpleDateFormat("MMM d, yyyy, H:mm:ss a", Locale.US);
            try {
                return json == null ? null : df.parse(json.getAsJsonPrimitive().getAsString());
            } catch (ParseException e) {
                throw new JsonParseException(e.getMessage());
            }
        }
    }
}
