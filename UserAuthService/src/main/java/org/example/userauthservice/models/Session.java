package org.example.userauthservice.models;


import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;

public class Session extends BaseModel {
    private String token;

    @ManyToOne(cascade = {CascadeType.REMOVE})
    private User user;
}