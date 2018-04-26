package org.upv.audiolibros.view.detail;

import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.service.Playback;

public interface PlayerInteractor extends Playback{
    void attach(AudioService aService);
    void detach();

    void load(Book book);

    boolean canPause();
    boolean canSeekBackward();
    boolean canSeekForward();
    int getBufferPercentage();
    int getAudioSessionId();
}
