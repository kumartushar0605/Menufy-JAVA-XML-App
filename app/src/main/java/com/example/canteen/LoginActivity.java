package com.example.canteen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    EditText resName, userName, password;
    ApiService apiService;
    public String resNamme;
    public String resImage;
    TextView resBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        resName = findViewById(R.id.restaurantName);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.loginButton);
        resBtn =findViewById(R.id.Registerr);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000") // Replace with your backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        loginBtn.setOnClickListener(v -> loginRes());

        resBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    void loginRes() {
        Log.d("LoginActivity","hiiiiiiiiiiiiiiiiLoginnnnnn");
        String UserName = userName.getText().toString().trim();
        String ResName = resName.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (UserName.isEmpty() || ResName.isEmpty() || Password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

//        RequestBody username = RequestBody.create(MediaType.parse("text/plain"), UserName);
//        RequestBody resname = RequestBody.create(MediaType.parse("text/plain"), ResName);
//        RequestBody pass = RequestBody.create(MediaType.parse("text/plain"), Password);
        LoginRequest loginRequest = new LoginRequest(ResName, UserName, Password);
       Log.d("LoginActivity","Loginpahase2222222222222222");
        Call<ResponseBody> call = apiService.loginRes(loginRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.d("LoginActivity","dddddddddddddd" +response.code());
                if (response.code() == 409) {
                    Log.d("LoginActivity", "Invalid Details");
                    Toast.makeText(LoginActivity.this, "Restaurant already registered", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 201) {

                    Log.d("LoginActivity", "Login successfully");
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    try{

                        String responseBody = response.body().string();
                        Log.d("LoginActivity", "Response Body: " + responseBody);
                        Gson gson = new Gson();
                        Responses responsee = gson.fromJson(responseBody,Responses.class);
                        String resName = responsee.getUser().getResName();
                        String ressImage = responsee.getUser().getImageName();
                        String imgurl = "http://10.0.2.2:5000/images/"+ressImage;
                        String token = responsee.getToken(); // Get token

                        SharedPreferences sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("RES_NAME", resName);
                        editor.putString("RES_IMAGE", imgurl);
                        editor.putBoolean("IS_LOGGED_IN", true);
                        editor.putString("TOKEN", token); // Store token
                        editor.apply();

//                       Log.d("LoginActivity",resName+"kkkkkkkkkkkkkkkkkkkkkkkk"+resImage);
//                        if (resName != null && resImage != null) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("resName", resName);
//                            intent.putExtra("resImage", resImage);

//                            Log.d("LoginActivity", "Intent extras - resName: " + intent.getStringExtra("resName") + ", resImage: " + intent.getStringExtra("resImage"));

                            startActivity(intent);
                            finish();
//                        } else {
//                            Log.e("LoginActivity", "resName or resImage is null");
//                        }

                    }catch (IOException e){
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
