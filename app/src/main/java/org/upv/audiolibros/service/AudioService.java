package org.upv.audiolibros.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import org.upv.audiolibros.MediaPlayerNotification;
import org.upv.audiolibros.model.Book;

import java.io.IOException;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;

public class AudioService extends Service implements Playback {
    private static final String TAG = "AudioService";
    // Binder given to clients
    private LocalBinder mBinder = new LocalBinder();
    private MediaPlayer player;
    private Book actualBook = Book.BOOK_EMPTY;
    private int lastBookPos;
    @Nullable
    private Callback callback;

    public class LocalBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        handleIntent(intent);
        return START_STICKY;
    }

    @Override
    public void load(final Book book){
        if(player.isPlaying()){
            player.stop();
        }
        this.actualBook = Book.BOOK_EMPTY;
        lastBookPos = 0;
        player.reset();

        String streamUrl = book.getUrlAudio();

        try {
            player.setDataSource(streamUrl);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    actualBook  = book;
                    if (callback != null) {
                        callback.onCompletion();
                    }
                }
            });

            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.e("ERROR","Error Playing Station");
                    if (callback != null) {
                        callback.onError("Error Playing");
                    }
                    return true;
                }
            });

            player.prepareAsync();

        } catch (IOException e) {
            Log.e(TAG, "play: ", e);
            if (callback != null) {
                callback.onError(e.getLocalizedMessage());
            }
        }

    }

    @Override
    public void play() {
        if(!Book.BOOK_EMPTY.equals(actualBook) && !player.isPlaying()){
            player.seekTo(lastBookPos);
            start();
        }
    }

    @Override
    public void start() {
        player.start();
        MediaPlayerNotification.notify(getApplicationContext(), actualBook, true);
        if (callback != null) {
            callback.onPlaybackStatusChanged(1);
        }
    }

    @Override
    public void stop() {
        player.stop();
        actualBook = Book.BOOK_EMPTY;
        lastBookPos = 0;
        if (callback != null) {
            callback.onPlaybackStatusChanged(-1);
        }
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getCurrentStreamPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return player.getDuration();
    }

    @Override
    public void pause(){
        lastBookPos = player.getCurrentPosition();
        player.pause();
        MediaPlayerNotification.notify(getApplicationContext(), actualBook, false);
        if (callback != null) {
            callback.onPlaybackStatusChanged(0);
        }
    }

    @Override
    public void seekTo(int position) {
        player.pause();
        player.seekTo(position);
        player.start();
    }

    @Override
    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    private void handleIntent(Intent intent){

        if(Book.BOOK_EMPTY.equals(actualBook)){
            String id = null;
            if(intent.getExtras() != null){
                id = intent.getExtras().getString(ARG_BOOK_ID, null);
            }
            if(id != null) {
                loadAndPlay(id);
            } else {
                Log.w(TAG, "handleIntent: No Book Id provided");
                return;
            }
        }

        String action = intent.getAction();
        if(action == null){
            Log.w(TAG, "handleIntent: Action is null");
            return;
        }
        switch (action){
            case "ACTION_TOGGLE":
                if(isPlaying()){
                    pause();
                } else {
                    play();
                }
                break;
            case "ACTION_PLAY":
                play();
                break;
            case "ACTION_STOP":
                pause();
                break;
            case "ACTION_DELETE":
                stop();
                break;
        }
    }

    //TODO: Terminar la implementacion de cargar y darle play
    private void loadAndPlay(String id) {

    }
}
