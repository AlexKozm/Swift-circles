package com.alexander.kozminykh.game.database;

/**
 * Created by kozmi on 2/11/2018.
 */

public class DatabaseSchema {
    public static final class DatabaseTable{
        public static final String NAME = "records";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String PLAYER_NAME = "gamerName";
            public static final String RECORD = "record";
            public static final String ACTIVE = "active";
        }
    }
}
