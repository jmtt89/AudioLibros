package org.upv.audiolibros.model.events;

import org.upv.audiolibros.model.Book;

import java.util.ArrayList;
import java.util.List;

public class BookEvent {
    public static final int InsertBook = 777;
    public static final int UpdateBook = 910;
    public static final int DeleteBook = 921;
    public static final int FetchBooks = 103;
    public static final int FetchBook  = 150;
    public static final int FetchLastBook = 164;
    public static final int Error  = 529;

    private int eventType = Error;
    private String error = "Uninitialized Event";
    private List<Book> books = new ArrayList<>();

    private BookEvent(Builder builder) {
        //TODO > Validar Construccion del Evento
        setEventType(builder.eventType);
        setError(builder.error);
        setBooks(builder.books);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(BookEvent copy) {
        Builder builder = new Builder();
        builder.eventType = copy.getEventType();
        builder.error = copy.getError();
        builder.books = copy.getBooks();
        return builder;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Book getBook() {
        return books.size() > 0 ? books.get(0) : Book.BOOK_EMPTY;
    }

    public List<Book> getBooks(){
        return books;
    }

    public void setBooks(List<Book> books){
        this.books.addAll(books);
    }

    public void addBook(Book book) {
        this.books.add(book);
    }

    public static final class Builder {
        private int eventType;
        private String error;
        private List<Book> books = new ArrayList<>();

        private Builder() {
        }

        public Builder withEventType(int eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder withError(String error) {
            this.error = error;
            return this;
        }

        public Builder withBook(Book book) {
            this.books.add(book);
            return this;
        }

        public Builder withBooks(List<Book> books) {
            this.books.addAll(books);
            return this;
        }

        public BookEvent build() {
            return new BookEvent(this);
        }
    }
}
