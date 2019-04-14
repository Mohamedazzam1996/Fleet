package com.fleetmanagment.service.http;

import com.fleetmanagment.FMApp;
import com.fleetmanagment.model.Workload;
import com.fleetmanagment.model.WorkloadData;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

public class WorkloadListAllApi extends BaseApi {

    private static final String PATH = "/api/workload/listAll/";

    public WorkloadListAllApi(final WorkloadListAllApiProtocol delegate) {
        this.delegate = delegate;
    }

    public void start(final String username) {
        final String url = DOMAIN + PATH;
        try {
            //handle = asyncHttpClient.post(FMApp.appContext, url, new StringEntity("{}"), "application/json", handler);
            handle = asyncHttpClient.post(FMApp.appContext, url, new StringEntity("{\"staff\":{\"name\":\""+username+"\"}}"), "application/json", handler);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Mohamed Assem

    @Override
    void parseJSON(final byte[] responseBody) {
        final String json = new String(responseBody);
        final Workload workload = getGson().fromJson(json, Workload.class);
        if (workload.error == null) {
            ((WorkloadListAllApiProtocol)delegate).workloadsReady(workload.data);
        }
        else {
            ((WorkloadListAllApiProtocol)delegate).listAllWorkloadsFailed(workload.error);
        }
    }

    public interface WorkloadListAllApiProtocol extends ApiProtocol {
        void workloadsReady(WorkloadData[] workloadData);
        void listAllWorkloadsFailed(String error);
    }
}
