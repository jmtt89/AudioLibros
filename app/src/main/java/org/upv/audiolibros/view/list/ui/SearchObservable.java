package org.upv.audiolibros.view.list.ui;

import android.support.v7.widget.SearchView;

import java.util.Observable;

public class SearchObservable extends Observable implements SearchView.OnQueryTextListener {

    @Override
    public boolean onQueryTextChange(String query) {
        setChanged();
        notifyObservers(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
