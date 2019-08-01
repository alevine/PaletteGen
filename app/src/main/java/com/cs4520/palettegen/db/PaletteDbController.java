package com.cs4520.palettegen.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class PaletteDbController {
    public static int RESPONSE_OK = 1;
    public static int RESPONSE_FAIL = -1;

    public static void addPalette(Palette palette, PaletteDbHelper dbHelper) {

        // Insert new Palette into database, get all Palettes and update adapter
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING, palette.getColorString());
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME, palette.getPaletteName());

        // Insert the new row, returning the primary key value of the new row
        palette.setId((int) db.insert(PaletteContract.PaletteEntry.TABLE_NAME, null, values));
    }

    public static Palette getPalette(int paletteId, PaletteDbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME,
                PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING
        };

        // Filter results WHERE "ID" = paletteId'
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = { Integer.toString(paletteId) };

        Cursor cursor = db.query(
                PaletteContract.PaletteEntry.TABLE_NAME,   // The table to query
                projection,                                // The array of columns to return (pass null to get all)
                selection,                                 // The columns for the WHERE clause
                selectionArgs,
                null,
                null,
                null
        );

        cursor.moveToNext();
        String colorString = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING));
        String name = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME));
        cursor.close();

        return new Palette(colorString, name, paletteId);
    }

    public static List<Palette> getAllPalettes(PaletteDbHelper dbHelper) {
        List<Palette> palettes = new ArrayList<>();

        // Database setup stuff
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PaletteContract.PaletteEntry.TABLE_NAME, null);

        // Boilerplate code to get all Palettes from the database.
        // Needed to show the database list on create of the main activity
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME));
                String colorstring = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING));
                int id = cursor.getInt(cursor.getColumnIndex(PaletteContract.PaletteEntry._ID));

                Palette selected = new Palette(colorstring, name, id);
                palettes.add(selected);

                cursor.moveToNext();
            }
        }
        cursor.close();

        return palettes;
    }

    public static void updatePalette(int paletteId, Palette palette, PaletteDbHelper dbHelper) {

        // Insert new Palette into database, get all Palettes and update adapter
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING, palette.getColorString());
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME, palette.getPaletteName());

        // Define 'where' part of query.
        String selection = PaletteContract.PaletteEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { Integer.toString(paletteId) };

        // Update entry for this palette.
        db.update(PaletteContract.PaletteEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public static void deletePalette(int paletteId, PaletteDbHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = PaletteContract.PaletteEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { Integer.toString(paletteId) };

        // Issue SQL statement.
        db.delete(PaletteContract.PaletteEntry.TABLE_NAME, selection, selectionArgs);
    }
}
