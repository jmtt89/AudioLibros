package org.upv.audiolibros.view.list;

public interface ListBooksRepository {
    void load();
    void loadLastBook();

    void delete(String bookId);
}
