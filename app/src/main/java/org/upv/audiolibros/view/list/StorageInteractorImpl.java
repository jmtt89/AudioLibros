package org.upv.audiolibros.view.list;

public class StorageInteractorImpl implements StorageInteractor {
    ListBooksRepository repository;

    public StorageInteractorImpl(ListBooksRepository repository) {
        this.repository = repository;
    }

    @Override
    public void loadBooks() {
        repository.load();
    }

    @Override
    public void loadLastBook() {
        repository.loadLastBook();
    }

    @Override
    public void deleteBook(String id) {
        repository.delete(id);
    }
}
