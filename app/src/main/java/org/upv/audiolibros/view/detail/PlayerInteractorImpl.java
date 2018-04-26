package org.upv.audiolibros.view.detail;

import android.support.annotation.Nullable;

import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.model.events.PlayerEvent;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.service.Playback;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerInteractorImpl implements PlayerInteractor {
    private Timer timer;
    @Nullable
    private AudioService service;
    private EventBus eventBus;

    private Book actual = Book.BOOK_EMPTY;

    public PlayerInteractorImpl(Timer timer, EventBus eventBus) {
        this.timer = timer;
        this.eventBus = eventBus;
    }

    @Override
    public void attach(AudioService service) {
        this.service = service;
        if(!Book.BOOK_EMPTY.equals(actual)){
            load(actual);
        }
    }

    @Override
    public void detach() {
        service = null;
    }

    @Override
    public void load(final Book book) {
        this.actual = book;
        setCallback(new Playback.Callback() {
            @Override
            public void onCompletion() {
                postStreamReady(book);
            }

            @Override
            public void onPlaybackStatusChanged(int state) {

            }

            @Override
            public void onError(String error) {
                post(error);
            }
        });
        if (service != null) {
            service.load(book);
        }
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void start() {
        if (service != null) {
            service.start();
            startTimer();
            postPlay();
        }
    }

    @Override
    public void play() {
        if (service != null) {
            service.play();
            startTimer();
            postPlay();
        }
    }

    @Override
    public void pause() {
        if (service != null) {
            service.pause();
            stopTimer();
            postPause();
        }
    }

    @Override
    public void stop() {
        if (service != null) {
            service.stop();
            stopTimer();
            postStop();
        }
    }

    @Override
    public boolean isPlaying() {
        return service != null && service.isPlaying();
    }

    @Override
    public int getCurrentStreamPosition() {
        return service != null ? service.getCurrentStreamPosition() : 0;
    }

    @Override
    public long getDuration() {
        return service != null ? service.getDuration() : 0;
    }

    @Override
    public void seekTo(int position) {
        if (service != null) {
            service.seekTo(position);
        }
    }

    @Override
    public void setCallback(Callback callback) {
        if (service != null) {
            service.setCallback(callback);
        }
    }

    // Seekbar

    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                postStreamPositionUpdate();
            }
        }, 0, 1000);
    }

    public void stopTimer(){
        timer.cancel();
    }

    // Eventbus Post

    private void post(String error) {
        PlayerEvent event = PlayerEvent.newBuilder().withEventType(PlayerEvent.Error).withError(error).build();
        eventBus.post(event);
    }

    private void postStreamReady(Book book) {
        PlayerEvent event = PlayerEvent.newBuilder().withEventType(PlayerEvent.Ready).withBook(book).build();
        eventBus.post(event);
    }

    private void postStreamPositionUpdate() {
        PlayerEvent event = PlayerEvent.newBuilder().withEventType(PlayerEvent.Update).withBook(actual).build();
        eventBus.post(event);
    }

    private void postPlay() {
        PlayerEvent event = PlayerEvent.newBuilder().withEventType(PlayerEvent.PlayBook).withBook(actual).build();
        eventBus.post(event);
    }

    private void postPause() {
        PlayerEvent event = PlayerEvent.newBuilder().withEventType(PlayerEvent.PauseBook).withBook(actual).build();
        eventBus.post(event);
    }

    private void postStop() {
        PlayerEvent event = PlayerEvent.newBuilder().withEventType(PlayerEvent.StopBook).withBook(actual).build();
        eventBus.post(event);
    }

}
