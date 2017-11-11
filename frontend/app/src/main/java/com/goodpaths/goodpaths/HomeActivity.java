package com.goodpaths.goodpaths;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private TextView counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        counter = findViewById(R.id.counter);

        findViewById(R.id.increment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCount();
    }

    private void increment() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:8080/counterinc";
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null != response) {
                            try {
                                int count = response.getInt("count");
                                counter.setText(""+count);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Error!", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }

    private void getCount() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.2.2:8080/counter";
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (null != response) {
                            try {
                                int count = response.getInt("count");
                                counter.setText(""+count);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Error!", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);
    }
}
