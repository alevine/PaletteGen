package com.cs4520.palettegen.db;

import android.provider.BaseColumns;

/**
 * Taken from the Android SQLite tutorial here:
 *
 * https://developer.android.com/training/data-storage/sqlite
 *
 */
public class PaletteContract {

    /* Private constructor to prevent instantiation */
    private PaletteContract() {

    }

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + PaletteEntry.TABLE_NAME + " (" +
                    PaletteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    PaletteEntry.COLUMN_NAME_COLORSTRING + " TEXT," +
                    PaletteEntry.COLUMN_NAME_PALETTE_NAME + " TEXT)";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PaletteEntry.TABLE_NAME;


    /* Inner class that defines the table contents */
    public static class PaletteEntry implements BaseColumns {
        public static final String TABLE_NAME = "palette";
        public static final String COLUMN_NAME_COLORSTRING = "colorstring";
        public static final String COLUMN_NAME_PALETTE_NAME = "name";
    }
}
