package com.fleetmanagment.service.http;

import com.fleetmanagment.FMApp;
import com.fleetmanagment.model.Staff;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class StaffApi extends BaseApi {
    private static final String PATH = "/api/staffData/listAll/";

    public StaffApi(final StaffApiProtocol delegate) {
        this.delegate = delegate;
    }

    public void start(final String username) {
        final String url = DOMAIN + PATH;
        try {
            handle = asyncHttpClient.post(FMApp.appContext, url, new StringEntity("{\"name\": \""+username+"\"}"), "application/json", handler);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    void parseJSON(final byte[] responseBody) {
        final String json = new String(responseBody);
        final Staff staff= getGson().fromJson(json, Staff.class);
        if( staff.error == null) {
            ((StaffApiProtocol)delegate).staffExist();
        }
        else {
            ((StaffApiProtocol)delegate).noStaff();
        }
    }

    public interface StaffApiProtocol extends ApiProtocol {
        void staffExist();
        void noStaff();
    }
}
