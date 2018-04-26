package org.upv.audiolibros.view.list.ui.clickEvents;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.upv.audiolibros.controller.BooksController;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.view.list.ui.adapter.BookRecyclerViewAdapter;

public class ExtraOptionsClickAction implements ClickAction {
    private final Context context;
    private final BookRecyclerViewAdapter adapter;

    public ExtraOptionsClickAction(Context context, BookRecyclerViewAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void execute(final Book book) {
        AlertDialog.Builder menu = new AlertDialog.Builder(context);
        CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
        menu.setItems(opciones, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int opcion) {
                switch (opcion) {
                    case 0: //Compartir
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, book.getTitle());
                        intent.putExtra(Intent.EXTRA_TEXT, book.getUrlAudio());
                        context.startActivity(Intent.createChooser(intent, "Compartir"));
                        break;
                    case 1: //Borrar
                        adapter.removeBook(book);
                        break;
                    case 2: //Insertar
                        adapter.addBook(new Book(book));
                        break;
                }
            }
        });
        menu.create().show();
    }
}
