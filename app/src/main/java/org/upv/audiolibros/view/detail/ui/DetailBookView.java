package org.upv.audiolibros.view.detail.ui;

import org.upv.audiolibros.model.Book;

import java.util.List;

public interface DetailBookView {
    void enableInputs();
    void disableInputs();

    void showProgressbar();
    void hideProgressbar();

    void showMediaController();
    void onStreamReady();
    void onSeekbarProgressUpdate();

    void fillBookData(Book book);
    void showError(String error);
}
