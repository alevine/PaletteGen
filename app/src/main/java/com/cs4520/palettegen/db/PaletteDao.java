package com.cs4520.palettegen.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PaletteDao {

    @Insert
    void insert(Palette... palettes);

    @Delete
    void deletePalettes(Palette... palettes);

    @Update
    void updateUsers(Palette... palettes);

    @Query("DELETE FROM palette_table")
    void deleteAll();

    @Query("SELECT * FROM palette_table")
    LiveData<List<Palette>> getAllPalettes();

    @Query("SELECT * FROM palette_table WHERE id = :id")
    Palette getPalette(int id);

    @Query("SELECT COUNT(*) FROM palette_table")
    int getPaletteCount();

}
