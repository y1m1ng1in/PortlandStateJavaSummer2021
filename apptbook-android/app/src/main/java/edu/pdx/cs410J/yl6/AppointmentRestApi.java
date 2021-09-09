package edu.pdx.cs410J.yl6;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AppointmentRestApi {

    @FormUrlEncoded
    @POST("login")
    Call<ApiResponseMessage> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("registration")
    Call<ApiResponseMessage> register(@Field("username") String username, @Field("password") String password,
                                      @Field("email") String email, @Field("address") String address);
}
