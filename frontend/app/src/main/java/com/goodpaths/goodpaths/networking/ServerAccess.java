package com.goodpaths.goodpaths.networking;


import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ServerAccess<Req, Resp> {

    public static final String BASE_URL = "http://10.0.2.2:8080";
    public static final int MY_SOCKET_TIMEOUT_MS = 100000;

    public interface OnResultHandler<Resp> {
        void onSuccess(Resp response);
        void onError();
    }

    private final int method;
    private final String url;
    private final Response.Listener<JSONObject> responseListener;
    private final Response.ErrorListener errorListener;
    private final RequestQueue requestQueue;
    private final ObjectMapper jsonObjectMapper;
    private final Class<Resp> tpe;


    public ServerAccess(Context context, int method, String url, final OnResultHandler<Resp> onResultHandler, Class<Resp> tpe) {
        this.method = method;
        this.url = url;
        this.jsonObjectMapper = new ObjectMapper();
        this.tpe = tpe;
        this.responseListener = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    onResultHandler.onSuccess(jsonToResponse(response));
                } catch (JSONException | IOException e) {
                    onResultHandler.onError();
                }
            }
        };

        this.errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResultHandler.onError();
            }
        };
        this.requestQueue = Volley.newRequestQueue(context);

    }

    public void makeRequest(Req data) throws ServerAccessException {
        try {
            JSONObject requestData = requestToJson(data);
            JsonObjectRequest request = new JsonObjectRequest(method, url, requestData, responseListener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);

        } catch (JSONException | IOException e) {
            throw new ServerAccessException("Failed to create JSON.", e);
        }
    }

    private JSONObject requestToJson(Req request) throws JSONException, JsonProcessingException {
        return new JSONObject(jsonObjectMapper.writeValueAsString(request));
    }
    private Resp jsonToResponse(JSONObject jsonObject) throws JSONException, IOException {
        return jsonObjectMapper.readValue(jsonObject.toString(), tpe);
    }

    public static class ServerAccessException extends Exception {
        public ServerAccessException(String message, Exception e) {
            super(message, e);
        }
    }
}
