package com.fleetmanagment.model;

import java.io.Serializable;

public class Login implements Serializable {
    public String status;
    public LoginData data;

    public class LoginData implements Serializable {
        public String id;
        public String access_token;
    }
}
