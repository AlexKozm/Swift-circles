package com.alexander.kozminykh.game.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.alexander.kozminykh.game.player.Player;

import java.util.UUID;

/**
 * Created by kozmi on 2/11/2018.
 */

public class MyCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public MyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Player getPlayer(){
        String uuidString = getString(getColumnIndex(DatabaseSchema.DatabaseTable.Cols.UUID));
        String name = getString(getColumnIndex(DatabaseSchema.DatabaseTable.Cols.PLAYER_NAME));
        int record = getInt(getColumnIndex(DatabaseSchema.DatabaseTable.Cols.RECORD));
        String getActive = getString(getColumnIndex(DatabaseSchema.DatabaseTable.Cols.ACTIVE));
        boolean active;
        /*if(Integer.parseInt(getActive) == 1){
            active = true;
        } else {
            active = false;
        }*/
        //same as ->
        active = Integer.parseInt(getActive) == 1;


        Player player = new Player(UUID.fromString(uuidString));
        player.setName(name);
        player.setRecord(record);
        player.setActive(active);
        return player;
    }
}
