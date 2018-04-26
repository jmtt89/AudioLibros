package org.upv.audiolibros.view.list;

import android.content.Context;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.model.events.BookEvent;

import java.util.List;

public class ListBooksRepositoryImpl implements ListBooksRepository {
    private Context context;
    private BooksDatabase database;
    private EventBus eventBus;

    public ListBooksRepositoryImpl(Context context, BooksDatabase database, EventBus eventBus) {
        this.context = context;
        this.database = database;
        this.eventBus = eventBus;
    }

    @Override
    public void load() {
        post(database.list());
    }

    @Override
    public void loadLastBook() {
        post(database.getLastBook());
    }

    @Override
    public void delete(String bookId) {
        database.delete(bookId);
        post();
    }



    private void post(){
        BookEvent event =
                BookEvent.newBuilder()
                        .withEventType(BookEvent.DeleteBook)
                        .build();
        eventBus.post(event);
    }

    private void post(List<Book> books){
        BookEvent event =
                BookEvent.newBuilder()
                        .withEventType(BookEvent.FetchBooks)
                        .withBooks(books)
                        .build();
        eventBus.post(event);
    }

    private void post(Book lastBook) {
        BookEvent event =
                BookEvent.newBuilder()
                        .withEventType(BookEvent.FetchLastBook)
                        .withBook(lastBook)
                        .build();
        eventBus.post(event);
    }

    private void post(String error){
        BookEvent event =
                BookEvent.newBuilder()
                        .withEventType(BookEvent.Error)
                        .withError(error)
                        .build();
        eventBus.post(event);
    }

    private void post(int eventType, String error, List<Book> books) {
        BookEvent event =
                BookEvent.newBuilder()
                        .withEventType(eventType)
                        .withError(error)
                        .withBooks(books)
                        .build();
        eventBus.post(event);
    }
}
