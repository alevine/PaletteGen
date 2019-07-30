package com.cs4520.palettegen.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PaletteDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PaletteGen.db";

    public PaletteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DB_HELPER", "On create db");
        db.execSQL(PaletteContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Upgrade policy is to simply to discard the data and start over
        Log.d("DB_HELPER", "On upgrade delete");
        db.execSQL(PaletteContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
