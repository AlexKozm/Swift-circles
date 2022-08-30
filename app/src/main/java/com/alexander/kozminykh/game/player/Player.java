package com.alexander.kozminykh.game.player;

import java.util.UUID;

/**
 * Created by kozmi on 2/11/2018.
 */

public class Player {
    private String mName;
    private int mRecord;
    private UUID mId;
    private Boolean active;

    public Player(){
        this(UUID.randomUUID());
        setName("Player");
        setActive(false);
        setRecord(0);
    }

    public Player(String name, Boolean active, int record){
        this(UUID.randomUUID());
        setName(name);
        setActive(active);
        setRecord(record);
    }

    public Player(UUID id){
        mId = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getRecord() {
        return mRecord;
    }

    public void setRecord(int record) {
        mRecord = record;
    }

}
