package org.upv.audiolibros.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.upv.audiolibros.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Book {
    private String id;
    private String title;
    private String author;
    private String urlCover;
    private String urlAudio;
    private Genre genre;     // Género literario
    private Boolean novedad; // Es una novedad
    private Boolean leido;   // Leído por el usuario
    private int vibrantColor = -1;
    private int mutedColor = -1;

    public Book(JSONObject json) {
        this.id = json.optString("id");
        this.title = json.optString("title");
        this.author = json.optString("author");
        this.urlCover = json.optString("urlCover");
        this.urlAudio = json.optString("urlAudio");

        switch (json.optString("genre", "DEF")){
            case "ALL":
                this.genre = Genre.ALL;
                break;
            case "EPIC":
                this.genre = Genre.EPIC;
                break;
            case "SUSPENSE":
                this.genre = Genre.SUSPENSE;
                break;
            case "XIX":
                this.genre = Genre.XIX;
                break;
            default:
                this.genre = Genre.ALL;
                break;
        }

        this.novedad = json.optBoolean("novedad");
        this.leido = json.optBoolean("leido");
        this.vibrantColor = json.optInt("vibrantColor", -1);
        this.mutedColor = json.optInt("mutedColor", -1);
    }

    public enum Genre{
        ALL, EPIC, XIX, SUSPENSE
    }

    public Book(String title, String author, String urlCover, String urlAudio, Genre genre, Boolean novedad, Boolean leido) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.urlCover = urlCover;
        this.urlAudio = urlAudio;
        this.genre = genre;
        this.novedad = novedad;
        this.leido = leido;
    }

    public Book(Book book) {
        this.id = UUID.randomUUID().toString();
        this.title = book.title;
        this.author = book.author;
        this.urlCover = book.urlCover;
        this.urlAudio = book.urlAudio;
        this.genre = book.genre;
        this.novedad = book.novedad;
        this.leido = book.leido;
        this.vibrantColor = book.vibrantColor;
        this.mutedColor = book.mutedColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrlCover() {
        return urlCover;
    }

    public void setUrlCover(String urlCover) {
        this.urlCover = urlCover;
    }

    public String getUrlAudio() {
        return urlAudio;
    }

    public void setUrlAudio(String urlAudio) {
        this.urlAudio = urlAudio;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Boolean getNovedad() {
        return novedad;
    }

    public void setNovedad(Boolean novedad) {
        this.novedad = novedad;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public int getVibrantColor() {
        return vibrantColor;
    }

    public void setVibrantColor(int vibrantColor) {
        this.vibrantColor = vibrantColor;
    }

    public int getMutedColor() {
        return mutedColor;
    }

    public void setMutedColor(int mutedColor) {
        this.mutedColor = mutedColor;
    }

    public String toJson(){
        JSONObject output = new JSONObject();
        try {
            output.put("id", id);
            output.put("title", title);
            output.put("author", author);
            output.put("urlCover", urlCover);
            output.put("urlAudio", urlAudio);
            output.put("genre", genre.name());
            output.put("novedad", novedad);
            output.put("leido", leido);
            output.put("vibrantColor", vibrantColor);
            output.put("mutedColor", mutedColor);
        } catch (JSONException e) {
            Log.e("BOOK", "toJson: ", e);
        }
        return output.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static List<Book> loadInitData() {
        final String server = "http://mmoviles.upv.es/audiolibros/";
        List<Book> books = new ArrayList<>();

        books.add(new Book("Kappa", "Akutagawa",
                server+"kappa.jpg", server+"kappa.mp3",
                Book.Genre.XIX, false, false));

        books.add(new Book("Avecilla", "Alas Clarín, Leopoldo",
                server+"avecilla.jpg", server+"avecilla.mp3",
                Book.Genre.XIX, true, false));

        books.add(new Book("Divina Comedia", "Dante",
                server+"divina_comedia.jpg", server+"divina_comedia.mp3",
                Book.Genre.EPIC, true, false));

        books.add(new Book("Viejo Pancho, El", "Alonso y Trelles, José",
                server+"viejo_pancho.jpg", server+"viejo_pancho.mp3",
                Book.Genre.XIX, true, true));

        books.add(new Book("Canción de Rolando", "Anónimo",
                server+"cancion_rolando.jpg", server+"cancion_rolando.mp3",
                Book.Genre.EPIC, false, true));

        books.add(new Book("Matrimonio de sabuesos", "Agata Christie",
                server+"matrim_sabuesos.jpg",server+"matrim_sabuesos.mp3",
                Book.Genre.SUSPENSE, false, true));

        books.add(new Book("La iliada", "Homero",
                server+"la_iliada.jpg", server+"la_iliada.mp3",
                Book.Genre.EPIC, true, false));

        return books;
    }
}
