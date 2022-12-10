package com.udacity.jc.critter.domain;

import jakarta.persistence.Entity;

@Entity
public class User extends Person{
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
