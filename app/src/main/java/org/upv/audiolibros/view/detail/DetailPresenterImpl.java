package org.upv.audiolibros.view.detail;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.model.events.BookEvent;
import org.upv.audiolibros.model.events.PlayerEvent;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.view.detail.ui.DetailBookView;

public class DetailPresenterImpl implements DetailPresenter {
    @Nullable
    private DetailBookView view;
    private EventBus eventBus;

    private PlayerInteractor playerInteractor;
    private StorageInteractor storageInteractor;

    public DetailPresenterImpl(@Nullable DetailBookView view, EventBus eventBus, PlayerInteractor playerInteractor, StorageInteractor storageInteractor) {
        this.view = view;
        this.eventBus = eventBus;
        this.playerInteractor = playerInteractor;
        this.storageInteractor = storageInteractor;
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
    public void loadBook(String bookId) {
        if (view != null) {
            view.disableInputs();
            view.showProgressbar();
        }
        storageInteractor.loadBook(bookId);
    }

    @Override
    public void showMediaController() {
        if (view != null) {
            view.showMediaController();
        }
    }

    @Override
    public void attach(AudioService aService) {
        playerInteractor.attach(aService);
    }

    @Override
    public void detach() {
        playerInteractor.detach();
    }

    @Override
    public void streamStart() {
        playerInteractor.start();
    }

    @Override
    public void streamPlay(Book book) {
        playerInteractor.play();
    }

    @Override
    public void streamPause() {
        playerInteractor.pause();
    }

    @Override
    public boolean streamIsPlaying() {
        return playerInteractor.isPlaying();
    }

    @Override
    public void streamSeek(int position) {
        playerInteractor.seekTo(position);
    }

    @Override
    public boolean streamCanPause() {
        return playerInteractor.canPause();
    }

    @Override
    public boolean streamCanSeekBackward() {
        return playerInteractor.canSeekBackward();
    }

    @Override
    public boolean streamCanSeekForward() {
        return playerInteractor.canSeekForward();
    }

    @Override
    public int streamBufferPercentage() {
        return playerInteractor.getBufferPercentage();
    }

    @Override
    public int streamCurrentPosition() {
        return playerInteractor.getCurrentStreamPosition();
    }

    @Override
    public long streamDuration() {
        return playerInteractor.getDuration();
    }

    @Override
    public int streamAudioSessionId() {
        return playerInteractor.getAudioSessionId();
    }

    @Override
    public void streamLoad(Book book) {
        playerInteractor.load(book);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BookEvent event){
        if(view != null) {
            view.enableInputs();
            view.hideProgressbar();
            switch (event.getEventType()) {
                case BookEvent.FetchBook:
                    Book book = event.getBook();
                    view.fillBookData(book);
                    break;
                default:
                case BookEvent.Error:
                    view.showError(event.getError());
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerEvent event) {
        if(view != null) {
            view.enableInputs();
            view.hideProgressbar();
            switch (event.getEventType()) {
                case PlayerEvent.Ready:
                    view.onStreamReady();
                    break;
                case PlayerEvent.Update:
                    view.onSeekbarProgressUpdate();
                    break;
                case PlayerEvent.StartBook:

                    break;
                case PlayerEvent.StopBook:

                    break;
                case PlayerEvent.PauseBook:

                    break;
                case PlayerEvent.PlayBook:

                    break;
                default:
                case PlayerEvent.Error:
                    view.showError(event.getError());
                    break;
            }
        }
    }
}
