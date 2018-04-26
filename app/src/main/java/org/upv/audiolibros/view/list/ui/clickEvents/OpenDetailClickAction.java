package org.upv.audiolibros.view.list.ui.clickEvents;

import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.view.list.ui.ListBooksView;

public class OpenDetailClickAction implements ClickAction {
    private final ListBooksView view;

    public OpenDetailClickAction(ListBooksView view) {
        this.view = view;
    }

    @Override
    public void execute(Book book) {
        view.showBookDetail(book);
    }
}
