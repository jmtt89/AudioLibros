package org.upv.audiolibros.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.upv.audiolibros.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BooksDatabaseSharedPref implements BooksDatabase {
    private static BooksDatabase self;
    private final String TAG = "BOOK_DB_PREF";
    private final String DB_NAME = "Books";
    private HashMap<String, Book> MAP;
    private SharedPreferences preferences;
    private SharedPreferences lastBookPreference;

    public static BooksDatabase getInstance(Context context){
        if(self == null){
            self = new BooksDatabaseSharedPref(context);
        }
        return self;
    }

    private BooksDatabaseSharedPref(Context context) {
        this.preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        this.lastBookPreference = context.getSharedPreferences("LastBooks", Context.MODE_PRIVATE);
        MAP = new HashMap<>();
        if(preferences.getAll().isEmpty()){
            for (Book book: Book.loadInitData()) {
                save(book);
            }
        }else{
            list();
        }
    }

    @Override
    public Book get(String id) {
        if(MAP.containsKey(id)){
            return MAP.get(id);
        }
        try {
            String json = preferences.getString(id, null);
            if(json != null && !json.isEmpty()){
                Book book = new Book(new JSONObject(json));
                MAP.put(id, book);
                return book;
            }
        } catch (JSONException e) {
            Log.e(TAG, "get: ", e);
        }
        return Book.BOOK_EMPTY;
    }

    @Override
    public List<Book> list() {
        return list(false);
    }

    @Override
    public List<Book> list(boolean force) {
        List<Book> books = new ArrayList<>();

        if(MAP.isEmpty() || force){
            MAP.clear();
            for (String key: preferences.getAll().keySet()) {
                try {
                    String json = preferences.getString(key,null);
                    if(json!= null && !json.isEmpty()){
                        Book book = new Book(new JSONObject(json));
                        MAP.put(book.getId(), book);
                        books.add(book);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "list: ", e);
                }
            }
        }else{
            books.addAll(MAP.values());
        }

        return books;
    }

    @Override
    public void save(Book book) {
        MAP.put(book.getId(), book);
        preferences.edit().putString(book.getId(), book.toJson()).apply();
    }

    @Override
    public void delete(String id) {
        MAP.remove(id);
        preferences.edit().remove(id).apply();
    }

    @Override
    public boolean hasLastBook() {
        return lastBookPreference.contains("LAST_BOOK");
    }

    @Override
    public void setLastBookId(String bookId) {
        SharedPreferences.Editor editor = lastBookPreference.edit();
        editor.putString("LAST_BOOK", bookId);
        editor.apply();
    }

    @Override
    public Book getLastBook() {
        String id = lastBookPreference.getString("LAST_BOOK", null);
        return get(id);
    }
}
