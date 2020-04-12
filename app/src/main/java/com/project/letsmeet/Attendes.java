package com.project.letsmeet;

import java.io.Serializable;

public class Attendes implements Serializable {

    private String id;
    private String userId;
    private String response;

    public Attendes(){

    }

    public Attendes(String userId, String response) {
        this.userId = userId;
        this.response = response;
    }

    public Attendes(String id, String userId, String response) {
        this.id = id;
        this.userId = userId;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
