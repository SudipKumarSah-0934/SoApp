package com.example.sample.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatePostDetailsModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("tag_id")
    @Expose
    private Object tagId;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("picture")
    @Expose
    private Object picture;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Object getTagId() {
        return tagId;
    }

    public void setTagId(Object tagId) {
        this.tagId = tagId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getPicture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }
}
