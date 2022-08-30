package com.alexander.kozminykh.game.player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexander.kozminykh.game.database.DatabaseSchema;
import com.alexander.kozminykh.game.database.DatabaseHelper;
import com.alexander.kozminykh.game.database.MyCursorWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kozmi on 2/11/2018.
 */

public class PlayerLab {
    private static PlayerLab sPlayerLab;
    private static SQLiteDatabase mDatabase;

    public static PlayerLab get(Context context){
        if(sPlayerLab == null) {
            sPlayerLab = new PlayerLab(context);
        }
        return sPlayerLab;
    }


    private PlayerLab(Context context){
        mDatabase = new DatabaseHelper(context).getWritableDatabase();
    }


    public void addPlayer(Player player) {
        ContentValues values = getContentValues(player);
        mDatabase.insert(DatabaseSchema.DatabaseTable.NAME, null, values);
    }


    public void updatePlayer(Player player) {
        String uuidString = player.getId().toString();
        ContentValues values = getContentValues(player);

        mDatabase.update(DatabaseSchema.DatabaseTable.NAME, values,
                DatabaseSchema.DatabaseTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }


    public void deletePlayer(Player player){
        String uuidString = player.getId().toString();
        ContentValues values = getContentValues(player);
        mDatabase.delete(DatabaseSchema.DatabaseTable.NAME, DatabaseSchema.DatabaseTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }


    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        MyCursorWrapper cursor = queryPlayer(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                players.add(cursor.getPlayer());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return players;
    }


    public void setPlayerActive(Player activePlayer){
        List<Player> players = getPlayers();
        for(int c = 0; players.size() - 1 >= c; c++){
            Player player = players.get(c);
            if(player.getId().equals(activePlayer.getId())){
                player.setActive(true);
                updatePlayer(player);
            } else {
                player.setActive(false);
                updatePlayer(player);
            }
        }
    }


    public Player getActivePlayer() {
        MyCursorWrapper cursor = queryPlayer(
                DatabaseSchema.DatabaseTable.Cols.ACTIVE + " = ?",
                new String[]{String.valueOf(1)}
        );
        try{
            if(cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPlayer();
        } finally {
            cursor.close();
        }
    }


    private MyCursorWrapper queryPlayer(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                DatabaseSchema.DatabaseTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new MyCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Player player){
        ContentValues values = new ContentValues();
        values.put(DatabaseSchema.DatabaseTable.Cols.UUID, player.getId().toString());
        values.put(DatabaseSchema.DatabaseTable.Cols.PLAYER_NAME, player.getName());
        values.put(DatabaseSchema.DatabaseTable.Cols.RECORD, player.getRecord());
        values.put(DatabaseSchema.DatabaseTable.Cols.ACTIVE, player.getActive() ? "1" : "0");
        return values;
    }



}












