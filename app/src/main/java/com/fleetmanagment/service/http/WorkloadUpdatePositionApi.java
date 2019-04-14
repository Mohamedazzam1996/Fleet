package com.fleetmanagment.service.http;

import com.fleetmanagment.FMApp;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class WorkloadUpdatePositionApi extends BaseApi {

    private static final String PATH = "/api/workload/trace/{id}/position/";

    public WorkloadUpdatePositionApi(final WorkloadUpdatePositionApiProtocol delegate) {
        this.delegate = delegate;
    }

    public void start(final String id, final Double lat, final Double lng, final String username) {
        final String url = DOMAIN + PATH.replace("{id}", id);
        try {
            handle = asyncHttpClient.post(FMApp.appContext, url, new StringEntity("{ \"latitude\":"+lat+",\"longitude\": "+lng+"}"), "application/json", handler);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    void parseJSON(final byte[] responseBody) {

    }

    public interface WorkloadUpdatePositionApiProtocol extends ApiProtocol {
    }
}
