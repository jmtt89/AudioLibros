package org.upv.audiolibros.view.list.ui;

import org.upv.audiolibros.model.Book;

import java.util.List;

public interface ListBooksView {
    void enableInputs();
    void disableInputs();

    void showProgressbar();
    void hideProgressbar();

    void showBookDetail(Book book);

    void addCollection(List<Book> books);
    void addBook(Book book);
    void updateBook(Book book);
    void deleteBook();

    void showError(String error);

}
