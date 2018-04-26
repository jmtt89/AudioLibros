package org.upv.audiolibros.view.list;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.events.BookEvent;
import org.upv.audiolibros.view.list.ui.ListBooksView;

public class ListBooksPresenterImpl implements ListBooksPresenter {
    @Nullable
    private ListBooksView view;
    private EventBus eventBus;
    private StorageInteractor interactor;

    public ListBooksPresenterImpl(ListBooksView view, EventBus eventBus, StorageInteractor interactor) {
        this.view = view;
        this.eventBus = eventBus;
        this.interactor = interactor;
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
    }

    @Override
    public void onResume() {
        eventBus.register(this);
    }

    @Override
    public void onPause() {
        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void loadLastBook() {
        if (view != null) {
            view.disableInputs();
            view.showProgressbar();
        }
        interactor.loadLastBook();
    }

    @Override
    public void loadBooks() {
        if (view != null) {
            view.disableInputs();
            view.showProgressbar();
        }
        interactor.loadBooks();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BookEvent event) {
        if(view != null) {
            view.enableInputs();
            view.hideProgressbar();
            switch (event.getEventType()) {
                case BookEvent.FetchBooks:
                    view.addCollection(event.getBooks());
                    break;
                case BookEvent.InsertBook:
                    view.addBook(event.getBook());
                    break;
                case BookEvent.UpdateBook:
                    view.updateBook(event.getBook());
                    break;
                case BookEvent.DeleteBook:
                    view.deleteBook();
                    break;
                case BookEvent.FetchLastBook:
                    view.showBookDetail(event.getBook());
                default:
                case BookEvent.Error:
                    view.showError(event.getError());
                    break;
            }
        }
    }
}
