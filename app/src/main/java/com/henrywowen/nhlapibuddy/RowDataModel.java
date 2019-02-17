package com.henrywowen.nhlapibuddy;

import android.widget.TableRow;

public class RowDataModel {

    private TableRow mTableRow;
    private TeamDataModel mTeamDataModel;

    public RowDataModel (TableRow r, TeamDataModel t){
        mTableRow = r;
        mTeamDataModel = t;
    }

    public TableRow getTableRow() {
        return mTableRow;
    }

    public TeamDataModel getTeamDataModel() {
        return mTeamDataModel;
    }
}
