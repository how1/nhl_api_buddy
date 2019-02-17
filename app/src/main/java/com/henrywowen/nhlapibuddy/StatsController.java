package com.henrywowen.nhlapibuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;



public class StatsController extends AppCompatActivity {

    public final String BASE_URL = "https://statsapi.web.nhl.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button standings = findViewById(R.id.standingsButton);

        standings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newStandingsIntent = new Intent(StatsController.this, StandingsController.class);
                startActivity(newStandingsIntent);
            }
        });

    }

    public static void getTeams(){
        String teamsURL = "/api/v1/teams";

    }
}
