package com.cs4520.palettegen.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Obtained most code from the following official tutorial:
 *
 * https://codelabs.developers.google.com/codelabs/android-room-with-a-view
 *
 * Modified to use a Palette instead.
 */
@Database(entities = {Palette.class}, version = 1, exportSchema = false)
public abstract class PaletteRoomDatabase extends RoomDatabase {

    public abstract PaletteDao paletteDao();

    private static volatile PaletteRoomDatabase INSTANCE;

    static PaletteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PaletteRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PaletteRoomDatabase.class, "palettes_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
