package com.fleetmanagment.service.http;

import com.fleetmanagment.FMApp;
import com.fleetmanagment.model.Login;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginApi extends BaseApi {
    private static final String PATH = "/rest/login/";

    public LoginApi(final LoginApiProtocol delegate) {
        this.delegate = delegate;
    }

    public void start(final String username, final String password) {
        final String url = DOMAIN + PATH;
        try {
            handle = asyncHttpClient.post(FMApp.appContext, url, new StringEntity("{\"userName\":\""+username+"\",\"password\":\""+password+"\"}"), "application/json", handler);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    void parseJSON(final byte[] responseBody) {
        final String json = new String(responseBody);
        final Login login = getGson().fromJson(json, Login.class);
        if( login == null) {
            ((LoginApiProtocol)delegate).loginFailed();
        }
        else {
            ((LoginApiProtocol)delegate).loginSucceeded(login);
        }
    }

    public interface LoginApiProtocol extends ApiProtocol {
        void loginSucceeded(Login login);
        void loginFailed();
    }
}
