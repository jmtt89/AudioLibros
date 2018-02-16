package org.upv.audiolibros.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.upv.audiolibros.AudioBooks;
import org.upv.audiolibros.MediaPlayerNotification;
import org.upv.audiolibros.R;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.model.Book;

import java.io.IOException;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;

public class AudioService extends Service implements Playback {
    private static final String TAG = "AudioService";
    // Binder given to clients
    private LocalBinder mBinder = new LocalBinder();
    private MediaPlayer player;
    private BooksDatabase database;
    private Book lastBook = null;
    private Book actualBook = null;
    private int lastBookPos;
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
        database = ((AudioBooks) getApplication()).getDatabase();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    private void play(final Book book, final OnAudioServiceListener listener){
        if(player.isPlaying()){
            player.stop();
        }
        player.reset();

        String streamUrl = book.getUrlAudio();

        try {
            player.setDataSource(streamUrl);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    MediaPlayerNotification.notify(getApplicationContext(), book, true);
                    if(lastBook == book && lastBookPos > 0){
                        player.seekTo(lastBookPos);
                    }
                    player.start();
                    lastBook = book;
                    actualBook = book;
                    getApplicationContext()
                            .getSharedPreferences("SESSION", Context.MODE_PRIVATE)
                            .edit()
                            .putString("LAST_STATION_ID", book.getId())
                            .apply();
                    listener.callback(true);
                }
            });

            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.e("ERROR","Error Playing Station");
                    lastBook = null;
                    actualBook = null;
                    Toast.makeText(getApplicationContext(), R.string.error_playing, Toast.LENGTH_LONG).show();
                    listener.callback(false);
                    return true;
                }
            });

            player.prepareAsync();

        } catch (IOException e) {
            Log.e(TAG, "play: ", e);
            callback.onError(e.getLocalizedMessage());
        }

    }

    public void play(String bookId){
        if(bookId == null){
            return;
        }
        //GetBook
        final Book book = database.get(bookId);
        //Reproduce
        play(book, new OnAudioServiceListener() {
            @Override
            public void callback(boolean success) {
                if(success){
                    MediaPlayerNotification.notify(getApplicationContext(), book, true);
                    callback.onCompletion();
                }else{
                    callback.onError("Error in Playing");
                }
            }
        });
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void stop() {
        actualBook = null;
        player.stop();
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
    public void play(Book book) {
        play(book, false);
    }

    @Override
    public void play(final Book book, boolean reset) {
        play(book, new OnAudioServiceListener() {
            @Override
            public void callback(boolean success) {
                if(success){
                    MediaPlayerNotification.notify(getApplicationContext(), book, true);
                    callback.onCompletion();
                }else{
                    callback.onError("Error playing audio");
                }
            }
        });
    }

    @Override
    public void pause(){
        actualBook = null;
        lastBookPos = player.getCurrentPosition();
        player.pause();
    }

    @Override
    public void seekTo(int position) {
        player.pause();
        player.seekTo(position);
        player.start();
//        player.seekTo(position);
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void handleIntent(Intent intent){
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String id = null;
        if(intent.getExtras() != null){
            id = intent.getExtras().getString(ARG_BOOK_ID, null);
        }

        if(id == null){
            if(lastBook == null){
                id = getApplicationContext()
                        .getSharedPreferences("SESSION", Context.MODE_PRIVATE)
                        .getString("LAST_STATION_ID", null);
            }
        }

        String action = intent.getAction();
        switch (action){
            case "ACTION_PLAY":
                if(id != null){
                    play(id);
                } else {
                    play(lastBook);
                }
                break;
            case "ACTION_STOP":
                pause();
                if(lastBook != null){
                    MediaPlayerNotification.notify(getApplicationContext(), lastBook, false);
                }
                break;
            case "ACTION_DELETE":
                pause();
                break;
        }
    }

    private interface OnAudioServiceListener {
        void callback(boolean success);
    }
}
