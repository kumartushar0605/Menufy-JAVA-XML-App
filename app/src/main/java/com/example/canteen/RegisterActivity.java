package com.example.canteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText restaurantNameEditText, userNameEditText, passwordEditText,addaddressText;
    TextView login;
    private Uri imageUri;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageView = findViewById(R.id.imageView);
        Toast.makeText(this, "Activity Started", Toast.LENGTH_SHORT).show();

        restaurantNameEditText = findViewById(R.id.restaurantName);
        userNameEditText = findViewById(R.id.userName);
        passwordEditText = findViewById(R.id.password);
        addaddressText = findViewById(R.id.address);
        Button registerButton = findViewById(R.id.registerButton);
        login  = findViewById(R.id.loginTextView);

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        apiService = retrofit.create(ApiService.class);

        imageView.setOnClickListener(v -> openImageSelector());
        registerButton.setOnClickListener(v -> registerUser());
        login.setOnClickListener(v -> activity());
    }
    void activity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).transform(new CircleCrop()).into(imageView);
        }
    }

    private void registerUser() {
        String restaurantName = restaurantNameEditText.getText().toString().trim();
        String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addaddressText.getText().toString().trim();

        if (restaurantName.isEmpty() || userName.isEmpty() || password.isEmpty() || imageUri == null) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = new File(getRealPathFromURI(imageUri));
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(this, "Error getting image file", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        MultipartBody.Part imageName = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        RequestBody resName = RequestBody.create(MediaType.parse("multipart/form-data"), restaurantName);
        RequestBody userNamee = RequestBody.create(MediaType.parse("multipart/form-data"), userName);
        RequestBody passwordd = RequestBody.create(MediaType.parse("multipart/form-data"), password);
        RequestBody addresss = RequestBody.create(MediaType.parse("multipart/form-data"),address);
        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();

        Log.d("RegisterActivity", "hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        Call<ResponseBody> call = apiService.registerUser(resName, userNamee, passwordd,addresss, imageName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    if (response.code()==201) {
                        String responseBody = response.body().string();
                        Log.d("RegisterActivity", "Registered");
                        Gson gson = new Gson();
                        Responses responsee = gson.fromJson(responseBody,Responses.class);
                        String resName = responsee.getUser().getResName();
                        String ressImage = responsee.getUser().getImageName();
                        String imgurl = "http://10.0.2.2:5000/images/"+ressImage;
                        String token = responsee.getToken(); // Get token
                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("RES_NAME", resName);
                        editor.putString("RES_IMAGE", imgurl);
                        editor.putBoolean("IS_LOGGED_IN", true);
                        editor.apply();
                        Intent intent = new Intent (RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(response.code()==409) {
                        Log.d("RegisterActivity", "Restaurant already registered");

                        Toast.makeText(RegisterActivity.this, "Restorent already registered", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
 Log.d("RegisterActivity","hiiiiiiii"+response.code());

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(column_index);
            }
            cursor.close();
        }

        if (filePath == null) {
            // Handle other types of URIs, such as "content" URIs
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    filePath = getTempFilename();
                    FileOutputStream outputStream = new FileOutputStream(filePath);
                    FileInputStream inputStream = new FileInputStream(fileDescriptor);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                    }
                    outputStream.close();
                    inputStream.close();
                    parcelFileDescriptor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }

    private String getTempFilename() {
        String tempFileName = System.currentTimeMillis() + ".jpg";
        File tempDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return new File(tempDir, tempFileName).getAbsolutePath();
    }
}
