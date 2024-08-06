package com.example.canteen;

import android.util.Log;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {


    @POST("/login") // Replace with the correct endpoint
    Call<ResponseBody> loginRes(@Body LoginRequest loginRequest);
    @Multipart
    @POST("/register")
    Call<ResponseBody> registerUser(
            @Part("resName") RequestBody resName,
            @Part("userNamee") RequestBody userName,
            @Part("passwordd") RequestBody password,
            @Part("addresss") RequestBody address,
            @Part MultipartBody.Part imageName
    );


}

