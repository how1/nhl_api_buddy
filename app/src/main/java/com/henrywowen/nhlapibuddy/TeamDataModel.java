package com.henrywowen.nhlapibuddy;

import android.util.Log;

import org.json.JSONObject;

import org.json.JSONException;

public class TeamDataModel {

    private String teamName;
    private String teamDiv;
    private String teamConf;
    private String teamLosses;
    private String teamWins;
    private String teamOT;
    private String teamGA;
    private String teamGF;
    private String teamPoints;
    private String teamDivisionRank;
    private String teamConferenceRank;
    private String teamLeagueRank;
    private String teamRow;
    private String teamGamesPlayed;
    private String teamStreakType;
    private String teamStreak;

    public static TeamDataModel fromJson(JSONObject jsonObject, String div, String conf){
        TeamDataModel teamDataModel = new TeamDataModel();
        teamDataModel.teamDiv = div;
        teamDataModel.teamConf = conf;
        try {
            teamDataModel.teamName = jsonObject.getJSONObject("team").getString("name");
            teamDataModel.teamLosses = ((Integer)jsonObject.getJSONObject("leagueRecord").getInt("losses")).toString();
            teamDataModel.teamWins = ((Integer)jsonObject.getJSONObject("leagueRecord").getInt("wins")).toString();
            teamDataModel.teamOT = ((Integer)jsonObject.getJSONObject("leagueRecord").getInt("ot")).toString();
            teamDataModel.teamGA = ((Integer)jsonObject.getInt("goalsAgainst")).toString();
            teamDataModel.teamGF = ((Integer)jsonObject.getInt("goalsScored")).toString();
            teamDataModel.teamPoints = ((Integer)jsonObject.getInt("points")).toString();
            teamDataModel.teamDivisionRank = jsonObject.getString("divisionRank");
            teamDataModel.teamConferenceRank = jsonObject.getString("conferenceRank");
            teamDataModel.teamLeagueRank = jsonObject.getString("leagueRank");
            teamDataModel.teamRow = ((Integer)jsonObject.getInt("row")).toString();
            teamDataModel.teamGamesPlayed = ((Integer)jsonObject.getInt("gamesPlayed")).toString();
            teamDataModel.teamStreakType = jsonObject.getJSONObject("streak").getString("streakType");
            teamDataModel.teamStreak = ((Integer)jsonObject.getJSONObject("streak").getInt("streakNumber")).toString();
        } catch (JSONException e){
            Log.d("NhlApiBuddy", "JSONException: " + e.toString());
        }
        return teamDataModel;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamLosses() {
        return teamLosses;
    }

    public String getTeamWins() {
        return teamWins;
    }

    public String getTeamOT() {
        return teamOT;
    }

    public String getTeamGA() {
        return teamGA;
    }

    public String getTeamGF() {
        return teamGF;
    }

    public String getTeamPoints() {
        return teamPoints;
    }

    public String getTeamDivisionRank() {
        return teamDivisionRank;
    }

    public String getTeamConferenceRank() {
        return teamConferenceRank;
    }

    public String getTeamLeagueRank() {
        return teamLeagueRank;
    }

    public String getTeamRow() {
        return teamRow;
    }

    public String getTeamGamesPlayed() {
        return teamGamesPlayed;
    }

    public String getTeamStreak() {
        return teamStreak;
    }

    public String getTeamStreakType() {
        return teamStreakType;
    }

    public String getTeamDiv() {
        return teamDiv;
    }

    public String getTeamConf() {
        return teamConf;
    }
}
