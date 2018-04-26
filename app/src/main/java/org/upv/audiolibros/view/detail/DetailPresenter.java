package org.upv.audiolibros.view.detail;

import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.service.AudioService;

public interface DetailPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroy();

    void loadBook(String bookId);

    void showMediaController();


    void attach(AudioService mService);
    void detach();


    void streamPlay(Book book);
    void streamSeek(int position);
    boolean streamCanPause();
    boolean streamCanSeekBackward();
    boolean streamCanSeekForward();
    int streamBufferPercentage();
    int streamCurrentPosition();
    long streamDuration();
    boolean streamIsPlaying();
    void streamPause();
    void streamStart();
    int streamAudioSessionId();

    void streamLoad(Book book);
}
