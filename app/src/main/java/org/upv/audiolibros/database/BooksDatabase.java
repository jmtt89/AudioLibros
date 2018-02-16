package org.upv.audiolibros.database;

import org.upv.audiolibros.model.Book;

import java.util.List;

public interface BooksDatabase {
    Book get(String id);
    List<Book> list();

    List<Book> list(boolean force);

    void save(Book book);
    void delete(String id);
}
