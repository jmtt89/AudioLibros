package org.upv.audiolibros.controller;

import android.content.Context;

import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.database.BooksDatabaseSharedPref;
import org.upv.audiolibros.model.Book;

import java.util.List;

public class BooksController {
    private static BooksController instance;
    private BooksDatabase database;

    private BooksController(Context context){
        database = BooksDatabaseSharedPref.getInstance(context);
    }

    public static BooksController getInstance(Context context) {
        if(instance == null){
            instance = new BooksController(context);
        }
        return instance;
    }

    public Book get(String id){
        return database.get(id);
    }

    public List<Book> list(){
        return database.list();
    }

    public List<Book> list(boolean force){
        return database.list(force);
    }

    public void save(Book book){
        database.save(book);
    }

    public void delete(String id){
        database.delete(id);
    }

    public boolean hasLastBook(){
        return database.hasLastBook();
    }

    public void setLastBookId(String bookId){
        database.setLastBookId(bookId);
    }

    public Book getLastBook(){
        return database.getLastBook();
    }

}
