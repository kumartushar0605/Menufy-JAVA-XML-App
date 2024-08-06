package com.example.canteen;

import com.google.gson.annotations.SerializedName;

public class Responses {
    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class User {
        @SerializedName("resName")
        private String resName;

        @SerializedName("imageName")
        private String imageName;

        // Getters and setters
        public String getResName() {
            return resName;
        }

        public void setResName(String resName) {
            this.resName = resName;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }
    }
}
