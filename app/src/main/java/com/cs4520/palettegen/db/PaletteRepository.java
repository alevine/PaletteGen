package com.cs4520.palettegen.db;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

class PaletteRepository {

    private PaletteDao paletteDao;
    private LiveData<List<Palette>> allPalettes;
    private MutableLiveData<Palette> searchResults = new MutableLiveData<>();

    PaletteRepository(Application application) {
        PaletteRoomDatabase db = PaletteRoomDatabase.getDatabase(application);
        paletteDao = db.paletteDao();
        allPalettes = paletteDao.getAllPalettes();
    }

    LiveData<List<Palette>> getAllPalettes() {
        return allPalettes;
    }

    MutableLiveData<Palette> getSearchResult() { return searchResults; }

    void getPalette(int id) {
        QueryAsyncTask task = new QueryAsyncTask(paletteDao);
        task.delegate = this;
        task.execute(id);
    }

    void insert(Palette palette) {
        new insertAsyncTask(paletteDao).execute(palette);
    }

    private void asyncFinished(Palette results) {
        searchResults.setValue(results);
    }

    private static class insertAsyncTask extends AsyncTask<Palette, Void, Void> {

        private PaletteDao mAsyncTaskDao;

        insertAsyncTask(PaletteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Palette... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class QueryAsyncTask extends AsyncTask<Integer, Void, Palette> {

        private PaletteDao asyncTaskDao;
        private PaletteRepository delegate = null;

        QueryAsyncTask(PaletteDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Palette doInBackground(final Integer... params) {
            return asyncTaskDao.getPalette(params[0]);
        }

        @Override
        protected void onPostExecute(Palette result) {
            delegate.asyncFinished(result);
        }
    }
}