package org.upv.audiolibros.view.list;

public interface ListBooksPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroy();

    void loadLastBook();
    void loadBooks();
}
