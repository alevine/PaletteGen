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
    private MutableLiveData<Integer> totalCount = new MutableLiveData<>();

    PaletteRepository(Application application) {
        PaletteRoomDatabase db = PaletteRoomDatabase.getDatabase(application);
        paletteDao = db.paletteDao();
        allPalettes = paletteDao.getAllPalettes();
    }

    LiveData<List<Palette>> getAllPalettes() {
        return allPalettes;
    }

    MutableLiveData<Palette> getSearchResult() { return searchResults; }

    MutableLiveData<Integer> getTotalCount() { return totalCount; }

    void getPalette(int id) {
        QueryAsyncTask task = new QueryAsyncTask(paletteDao);
        task.delegate = this;
        task.execute(id);
    }

    void count() {
        CountAsyncTask task = new CountAsyncTask(paletteDao);
        task.delegate = this;
        task.execute();
    }

    void insert(Palette palette) {
        new insertAsyncTask(paletteDao).execute(palette);
    }

    private void asyncFinished(Palette results) {
        searchResults.setValue(results);
    }

    private void asyncFinished(Integer result) {
        totalCount.setValue(result);
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

    private static class CountAsyncTask extends AsyncTask<Void, Void, Integer> {

        private PaletteDao asyncTaskDao;
        private PaletteRepository delegate = null;

        CountAsyncTask(PaletteDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return asyncTaskDao.getPaletteCount();
        }

        @Override
        protected void onPostExecute(Integer result) {
            delegate.asyncFinished(result);
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