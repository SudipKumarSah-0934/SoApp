package com.example.sample.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatePostModel {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private CreatePostDetailsModel data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreatePostDetailsModel getData() {
        return data;
    }

    public void setData(CreatePostDetailsModel data) {
        this.data = data;
    }


}
