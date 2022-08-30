package com.alexander.kozminykh.game.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kozmi on 2/11/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "gameBase.db";
    private Context mContext;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DatabaseSchema.DatabaseTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                DatabaseSchema.DatabaseTable.Cols.UUID + ", " +
                DatabaseSchema.DatabaseTable.Cols.PLAYER_NAME + ", " +
                DatabaseSchema.DatabaseTable.Cols.RECORD + ", " +
                DatabaseSchema.DatabaseTable.Cols.ACTIVE + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
