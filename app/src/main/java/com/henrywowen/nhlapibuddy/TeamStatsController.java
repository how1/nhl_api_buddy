package com.henrywowen.nhlapibuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;

public class TeamStatsController extends AppCompatActivity {

    private TableLayout mTableLayout;
    private ArrayList<TeamDataModel> teams;
    private String currentSort = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_stats_controller);
        mTableLayout = findViewById(R.id.tableLayout);
        setupTable();
        this.getStandings();
    }

    public void getStandings(){
        final String standingsURL = "https://statsapi.web.nhl.com/api/v1/standings";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(standingsURL, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
//                WeatherDataModel weatherDataModel = WeatherDataModel.fromJson(response);
//                Log.d("NhlApiBuddy", response.toString());
//                updateUI(weatherDataModel);
                processStandings(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("NhlApiBuddy", e.toString());
                Log.d("NhlApiBuddy", "Status Code: " + statusCode);
//                Toast.makeText(TeamStatsController.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //Parse json response
    public void processStandings(JSONObject response){
        teams = new ArrayList<>();
        try {
            JSONArray records = response.getJSONArray("records");
            for (int i = 0; i < records.length(); i++) {    //4                                 //For each division
                JSONObject divisionRecords = records.getJSONObject(i);                  //There are four divisions
                JSONArray teamRecords = divisionRecords.getJSONArray("teamRecords");
                for (int j = 0; j < teamRecords.length(); j++){ //8ish                      //Each division has a list of team records
                    JSONObject team = teamRecords.getJSONObject(j);
                    teams.add(TeamDataModel.fromJson(team));
                }
            }
            this.updateUI(teams);
        } catch (JSONException e){
            Log.e("NhlApiBuddy", e.toString());
        }

    }

    public void updateUI(ArrayList<TeamDataModel> teams){
        //Removing old table items
        if (mTableLayout.getChildCount() > 1){
            mTableLayout.removeViews(1, mTableLayout.getChildCount() -1);
        }
        for(TeamDataModel team: teams){
            //Log.d("NhlApiBuddy", team.toString());
            TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView teamName = new TextView(this);
            TextView teamGamesPlayed = new TextView(this);
            TextView teamWins = new TextView(this);
            TextView teamLosses = new TextView(this);
            TextView teamOT = new TextView(this);
            TextView teamGoalsFor = new TextView(this);
            TextView teamGoalsAgainst = new TextView(this);
            TextView teamPoints = new TextView(this);
            TextView teamDivisionRank = new TextView(this);
            TextView teamConferenceRank = new TextView(this);
            TextView teamLeagueRank = new TextView(this);
            TextView teamRow = new TextView(this);
            TextView teamStreak = new TextView(this);
            teamName.setText(team.getTeamName());
            teamGamesPlayed.setText(team.getTeamGamesPlayed());
            teamWins.setText(team.getTeamWins());
            teamLosses.setText(team.getTeamLosses());
            teamOT.setText(team.getTeamOT());
            teamGoalsFor.setText(team.getTeamGF());
            teamGoalsAgainst.setText(team.getTeamGA());
            teamPoints.setText(team.getTeamPoints());
            teamDivisionRank.setText(team.getTeamDivisionRank());
            teamConferenceRank.setText(team.getTeamConferenceRank());
            teamLeagueRank.setText(team.getTeamLeagueRank());
            teamRow.setText(team.getTeamRow());
            teamStreak.setText(team.getTeamStreakType().substring(0,1).toUpperCase() + team.getTeamStreak());
            row.addView(teamName);
            row.addView(teamGamesPlayed);
            row.addView(teamWins);
            row.addView(teamLosses);
            row.addView(teamOT);
            row.addView(teamGoalsFor);
            row.addView(teamGoalsAgainst);
            row.addView(teamPoints);
            row.addView(teamDivisionRank);
            row.addView(teamConferenceRank);
            row.addView(teamLeagueRank);
            row.addView(teamRow);
            row.addView(teamStreak);
            mTableLayout.addView(row);
        }
    }

    public void setupTable(){
        //Top row
        ArrayList<Button> columns = new ArrayList<Button>();
        columns.add((Button) findViewById(R.id.name_column));
        columns.add((Button) findViewById(R.id.gp_column));
        columns.add((Button) findViewById(R.id.w_column));
        columns.add((Button) findViewById(R.id.l_column));
        columns.add((Button) findViewById(R.id.ot_column));
        columns.add((Button) findViewById(R.id.gf_column));
        columns.add((Button) findViewById(R.id.ga_column));
        columns.add((Button) findViewById(R.id.pts_column));
        columns.add((Button) findViewById(R.id.dr_column));
        columns.add((Button) findViewById(R.id.cr_column));
        columns.add((Button) findViewById(R.id.lr_column));
        columns.add((Button) findViewById(R.id.row_column));
        columns.add((Button) findViewById(R.id.str_column));

        //Add OnClickListeners for sorting
        for(Button v: columns){
//            v.setClickable(true);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortTeams(v.getTag().toString());
                    updateUI(teams);
                }
            });
        }



    }

    public void sortTeams(String t){
        Log.d("NhlApiBuddy", "Sort by: " + t);
        final String tag = t;
        if (tag.equals(currentSort)){
            Collections.reverse(teams);
            return;
        } else {
            currentSort = tag;
            Collections.sort(teams, new Comparator<TeamDataModel>() {
                @Override
                public int compare(TeamDataModel o1, TeamDataModel o2) {
                    switch (tag){
                        case "name":
                            return o1.getTeamName().compareTo(o2.getTeamName());
                        case "gp":
                            return Integer.valueOf(o1.getTeamGamesPlayed()).compareTo(Integer.valueOf(o2.getTeamGamesPlayed()));
                        case "w":
                            return Integer.valueOf(o1.getTeamWins()).compareTo(Integer.valueOf(o2.getTeamWins()));
                        case "l":
                            return Integer.valueOf(o1.getTeamLosses()).compareTo(Integer.valueOf(o2.getTeamLosses()));
                        case "ot":
                            return Integer.valueOf(o1.getTeamOT()).compareTo(Integer.valueOf(o2.getTeamOT()));
                        case "gf":
                            return Integer.valueOf(o1.getTeamGF()).compareTo(Integer.valueOf(o2.getTeamGF()));
                        case "ga":
                            return Integer.valueOf(o1.getTeamGA()).compareTo(Integer.valueOf(o2.getTeamGA()));
                        case "pts":
                            return Integer.valueOf(o1.getTeamPoints()).compareTo(Integer.valueOf(o2.getTeamPoints()));
                        case "dr":
                            return Integer.valueOf(o1.getTeamDivisionRank()).compareTo(Integer.valueOf(o2.getTeamDivisionRank()));
                        case "cr":
                            return Integer.valueOf(o1.getTeamConferenceRank()).compareTo(Integer.valueOf(o2.getTeamConferenceRank()));
                        case "lr":
                            return Integer.valueOf(o1.getTeamLeagueRank()).compareTo(Integer.valueOf(o2.getTeamLeagueRank()));
                        case "row":
                            return Integer.valueOf(o1.getTeamRow()).compareTo(Integer.valueOf(o2.getTeamRow()));
                        case "str":
                            Double o1Streak = Double.valueOf(o1.getTeamStreak());
                            Double o2Streak = Double.valueOf(o2.getTeamStreak());
                            if (o1.getTeamStreakType().equals("losses")){
                                o1Streak = -o1Streak;
                                Log.d("NhlApiBuddy", o1Streak.toString());
                            }
                            if (o2.getTeamStreakType().equals("losses")){
                                o2Streak = -o2Streak;
                                Log.d("NhlApiBuddy", o1Streak.toString());
                            }
                            if (o1.getTeamStreakType().equals("ot")){
                                o1Streak *= 0.5;
                            }
                            if (o2.getTeamStreakType().equals("ot")){
                                o2Streak *= 0.5;
                            }
                            return o1Streak.compareTo(o2Streak);
                    }
                    return 0;
                }
            });
        }
        Collections.reverse(teams);
    }
}
