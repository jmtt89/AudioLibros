package org.upv.audiolibros.view.list;

public interface StorageInteractor {
    void loadBooks();
    void loadLastBook();

    void deleteBook(String id);
}
