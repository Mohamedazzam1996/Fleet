package com.fleetmanagment.service.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import cz.msebera.android.httpclient.Header;

public abstract class BaseApi {
    static final String DOMAIN = "http://node209344-env-3584806.j.layershift.co.uk";
    private static Gson gson;

    static AsyncHttpClient asyncHttpClient;
    AsyncHttpResponseHandler handler;
    RequestHandle handle;
    ApiProtocol delegate;

    BaseApi() {
        if( asyncHttpClient == null ) {
            asyncHttpClient = new AsyncHttpClient();
        }
        asyncHttpClient.setURLEncodingEnabled(false);
        asyncHttpClient.setMaxConnections(1);
        asyncHttpClient.setMaxRetriesAndTimeout(3, 1500);
        asyncHttpClient.addHeader(AsyncHttpClient.HEADER_ACCEPT_ENCODING, AsyncHttpClient.ENCODING_GZIP);

        handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final byte[] responseBody) {
                parseJSON(responseBody);
            }

            @Override
            public void onFailure(final int statusCode, final Header[] headers, final byte[] responseBody, final Throwable error) {
                delegate.apiFailed("HTTP-Error: "+statusCode);
            }

            @Override
            public void onStart() {
                delegate.apiStarted();
            }

            @Override
            public void onFinish() {
                delegate.apiFinished();
            }
        };
    }

    public void stop() {
        if( handle != null ) {
            handle.cancel(true);
        }
    }

    abstract void parseJSON(byte[] responseBody);

    static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().disableHtmlEscaping().setLenient().create();
        }
        return gson;
    }

    interface ApiProtocol {
        void apiStarted();
        void apiFinished();
        void apiFailed(String error);
    }
}