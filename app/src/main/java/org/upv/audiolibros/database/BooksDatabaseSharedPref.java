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
    private final String TAG = "BOOK_DB_PREF";
    private final String DB_NAME = "Books";
    private HashMap<String, Book> MAP;
    private SharedPreferences preferences;

    public BooksDatabaseSharedPref(Context context) {
        this.preferences = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
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
        return null;
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
}
