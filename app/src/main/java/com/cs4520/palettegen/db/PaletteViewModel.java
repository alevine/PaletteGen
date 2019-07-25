package com.cs4520.palettegen.db;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class PaletteViewModel extends AndroidViewModel {

    private PaletteRepository mRepository;

    private LiveData<List<Palette>> mAllPalettes;

    private MutableLiveData<Palette> searchResults;

    public PaletteViewModel (Application application) {
        super(application);
        this.mRepository = new PaletteRepository(application);
        mAllPalettes = this.mRepository.getAllPalettes();
        searchResults = this.mRepository.getSearchResult();
    }

    public MutableLiveData<Palette> getSearchResults() {
        return this.searchResults;
    }

    public LiveData<List<Palette>> getAllPalettes() { return this.mAllPalettes; }

    public void getPalette(int id) { this.mRepository.getPalette(id); }

    public void insert(Palette palette) { this.mRepository.insert(palette); }
}
