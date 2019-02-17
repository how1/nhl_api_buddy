package com.henrywowen.nhlapibuddy;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;

public class StandingsController extends AppCompatActivity {

    private TableLayout mTableLayout;
    private ArrayList<TeamDataModel> teams;
    private ArrayList<RowDataModel> leagueRows;
    private ArrayList<RowDataModel> atlRows;
    private ArrayList<RowDataModel> metRows;
    private ArrayList<RowDataModel> cenRows;
    private ArrayList<RowDataModel> pacRows;
    private ArrayList<RowDataModel> eastRows;
    private ArrayList<RowDataModel> westRows;
    private String currentSort = "pts";
    private String organizedBy = "division";
    private Boolean mDoneUpdatingUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings_controller);
        mTableLayout = findViewById(R.id.tableLayout);
        setupTable();

        leagueRows = new ArrayList<>();
        atlRows = new ArrayList<>();
        metRows = new ArrayList<>();
        cenRows = new ArrayList<>();
        pacRows = new ArrayList<>();
        eastRows = new ArrayList<>();
        westRows = new ArrayList<>();

        Button divButton = findViewById(R.id.division_standings);
        Button confButton = findViewById(R.id.conference_standings);
        Button leaButton = findViewById(R.id.league_standings);

        divButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizedBy = "division";
                mTableLayout.addView(getTableDivider("Atlantic"));
                updateUI(atlRows);
                mTableLayout.addView(getTableDivider("Metropolitan"));
                updateUI(metRows);
                mTableLayout.addView(getTableDivider("Central"));
                updateUI(cenRows);
                mTableLayout.addView(getTableDivider("Pacific"));
                updateUI(pacRows);
                mDoneUpdatingUI = true;
            }
        });

        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizedBy = "conference";
                sortTeams("pts", eastRows);
                sortTeams("pts", westRows);
                mTableLayout.addView(getTableDivider("Eastern"));
                updateUI(eastRows);
                mTableLayout.addView(getTableDivider("Western"));
                updateUI(westRows);
                mDoneUpdatingUI = true;
            }
        });

        leaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                organizedBy = "league";
                sortTeams("pts", leagueRows);
                updateUI(leagueRows);
                mDoneUpdatingUI = true;
            }
        });

        getStandings();

    }

    //Retrieve standings from api
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
                String div = divisionRecords.getJSONObject("division").getString("nameShort");
                String conf = divisionRecords.getJSONObject("conference").getString("name");
                JSONArray teamRecords = divisionRecords.getJSONArray("teamRecords");
                for (int j = 0; j < teamRecords.length(); j++){ //8ish                      //Each division has a list of team records
                    JSONObject team = teamRecords.getJSONObject(j);
                    teams.add(TeamDataModel.fromJson(team, div, conf));
                }
            }
            createRows(teams);
            if (organizedBy.equals("league")){
                clearUI();
                mTableLayout.addView(getTableDivider("League"));
                this.updateUI(leagueRows);
            } else if (organizedBy.equals("division")){
                clearUI();
                mTableLayout.addView(getTableDivider("Atlantic"));
                this.updateUI(atlRows);
                mTableLayout.addView(getTableDivider("Metropolitan"));
                this.updateUI(metRows);
                mTableLayout.addView(getTableDivider("Central"));
                this.updateUI(cenRows);
                mTableLayout.addView(getTableDivider("Pacific"));
                this.updateUI(pacRows);
            } else if (organizedBy.equals("conference")){
                clearUI();
                mTableLayout.addView(getTableDivider("Eastern"));
                this.updateUI(eastRows);
                mTableLayout.addView(getTableDivider("Western"));
                this.updateUI(westRows);
            }

        } catch (JSONException e){
            Log.e("NhlApiBuddy", e.toString());
        }

    }

    public void clearUI(){
        mTableLayout.removeViews(1, mTableLayout.getChildCount() -1);
    }

    public void updateUI(ArrayList<RowDataModel> rows){
        int i = 0;
        for (RowDataModel row: rows){
            if (row.getTableRow().getParent() != null){
                mTableLayout.removeView(row.getTableRow());
            }
            mTableLayout.addView(row.getTableRow());
            if (i % 2 == 0){
                row.getTableRow().setBackgroundColor(Color.LTGRAY);
            } else {
                row.getTableRow().setBackgroundColor(Color.WHITE);
            }
            i++;
        }
    }

    public TableRow getTableDivider(String label){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView rowLabel = new TextView(this);
        rowLabel.setText(label);
        rowLabel.setTextColor(Color.WHITE);
        row.addView(rowLabel);
        row.setBackgroundColor(Color.DKGRAY);
        return row;
    }

    public void createRows(ArrayList<TeamDataModel> teams){
        for(TeamDataModel team: teams){
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
            if (teams.indexOf(team) % 2 == 0){
                row.setBackgroundColor(Color.LTGRAY);
            }
            RowDataModel rowDataModel = new RowDataModel(row, team);
            leagueRows.add(rowDataModel);
            switch (team.getTeamDiv()){
                case "ATL": atlRows.add(rowDataModel);
                break;
                case "Metro": metRows.add(rowDataModel);
                break;
                case "CEN": cenRows.add(rowDataModel);
                break;
                case "PAC": pacRows.add(rowDataModel);
                break;
            }
            switch (team.getTeamConf()){
                case "Eastern": eastRows.add(rowDataModel);
                break;
                case "Western": westRows.add(rowDataModel);
                break;
            }
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
                    if (organizedBy.equals("league")){
                        sortTeams(v.getTag().toString(), leagueRows);
                        clearUI();
                        mTableLayout.addView(getTableDivider("League"));
                        updateUI(leagueRows);
                    } else if (organizedBy.equals("division")){
                        sortTeams(v.getTag().toString(), atlRows);
                        sortTeams(v.getTag().toString(), metRows);
                        sortTeams(v.getTag().toString(), cenRows);
                        sortTeams(v.getTag().toString(), pacRows);
                        clearUI();
                        mTableLayout.addView(getTableDivider("Atlantic"));
                        updateUI(atlRows);
                        mTableLayout.addView(getTableDivider("Metropolitan"));
                        updateUI(metRows);
                        mTableLayout.addView(getTableDivider("Central"));
                        updateUI(cenRows);
                        mTableLayout.addView(getTableDivider("Pacific"));
                        updateUI(pacRows);
                    } else if (organizedBy.equals("conference")){
                        sortTeams(v.getTag().toString(), eastRows);
                        sortTeams(v.getTag().toString(), westRows);
                        clearUI();
                        mTableLayout.addView(getTableDivider("Eastern"));
                        updateUI(eastRows);
                        mTableLayout.addView(getTableDivider("Pacific"));
                        updateUI(westRows);
                    }
                }
            });
        }
    }

    public void sortTeams(String t, ArrayList<RowDataModel> rows){
        final String tag = t;
        if (tag.equals(currentSort)){
            Collections.reverse(rows);
            return;
        } else {
            currentSort = tag;
            Collections.sort(rows, new Comparator<RowDataModel>() {
                @Override
                public int compare(RowDataModel r1, RowDataModel r2) {
                    TeamDataModel o1 = r1.getTeamDataModel();
                    TeamDataModel o2 = r2.getTeamDataModel();
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
