package org.upv.audiolibros.view.detail;

import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.model.events.BookEvent;

public class DetailRepositoryImpl implements DetailRepository {
    private BooksDatabase database;
    private EventBus eventBus;

    public DetailRepositoryImpl(BooksDatabase database, EventBus eventBus) {
        this.database = database;
        this.eventBus = eventBus;
    }

    @Override
    public void load(String bookId) {
        post(database.get(bookId));
    }

    private void post(Book book) {
        BookEvent event =
                BookEvent.newBuilder()
                        .withEventType(BookEvent.FetchBook)
                        .withBook(book)
                        .build();
        eventBus.post(event);
    }
}
