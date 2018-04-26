package org.upv.audiolibros.model.events;

import org.upv.audiolibros.model.Book;

public class PlayerEvent {
    public static final int Ready     = 200;
    public static final int Update    = 300;
    public static final int StartBook = 101;
    public static final int PlayBook  = 202;
    public static final int PauseBook = 303;
    public static final int StopBook  = 404;
    public static final int Error     = 38808;

    private int eventType = Error;
    private String error = "Uninitialized Event";
    private Book book = Book.BOOK_EMPTY;

    private PlayerEvent(Builder builder) {
        //TODO > Validar Construccion del Evento
        setEventType(builder.eventType);
        setError(builder.error);
        setBook(builder.book);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(PlayerEvent copy) {
        Builder builder = new Builder();
        builder.eventType = copy.getEventType();
        builder.error = copy.getError();
        builder.book = copy.getBook();
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
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public static final class Builder {
        private int eventType;
        private String error;
        private Book book;

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
            this.book = book;
            return this;
        }

        public PlayerEvent build() {
            return new PlayerEvent(this);
        }
    }
}
