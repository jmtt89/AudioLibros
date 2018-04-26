package org.upv.audiolibros.view.detail;

public class StorageInteractorImpl implements StorageInteractor {
    DetailRepository repository;

    public StorageInteractorImpl(DetailRepository repository) {
        this.repository = repository;
    }

    @Override
    public void loadBook(String bookId) {
        repository.load(bookId);
    }
}
