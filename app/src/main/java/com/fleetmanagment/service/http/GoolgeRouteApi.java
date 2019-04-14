package com.fleetmanagment.service.http;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GoolgeRouteApi extends BaseApi {

    public GoolgeRouteApi(GoolgeRouteApiProtocol delegate) {
        this.delegate = delegate;
    }

    public void start(final LatLng origin, final LatLng dest) {
        final String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        final String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        final String sensor = "sensor=false";
        final String mode = "mode=driving";
        final String key = "key=AIzaSyCl4Y7_ix9HAAT8XXIdmlWUaVors29Phx4";
        final String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;
        final String output = "json";
        final String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        handle = asyncHttpClient.get(url, handler);
    }

    @Override
    void parseJSON(final byte[] responseBody) {
        try {
            final JSONObject jObject = new JSONObject(new String(responseBody));
            final DataParser parser = new DataParser();
            final List<List<HashMap<String, String>>> routes = parser.parse(jObject);
            ((GoolgeRouteApiProtocol)delegate).routesReady(routes);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public interface GoolgeRouteApiProtocol extends ApiProtocol {
        void routesReady(List<List<HashMap<String, String>>> routes);
    }
}

