package org.upv.audiolibros.service;


import org.upv.audiolibros.model.Book;

public interface Playback {
    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     */
    void stop();

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    boolean isPlaying();

    /**
     * @return pos if currently playing an item
     */
    int getCurrentStreamPosition();

    /**
     * @return duration if currently playing an item
     */
    long getDuration();

    /**
     * @param book to play
     */
    void load(Book book);

    /**
     * @param book to play
     * @param reset true to always start from beginning
     */
    void play();

    /**
     * Pause the current playing item
     */
    void pause();

    /**
     * Seek to the given position
     */
    void seekTo(int position);

    interface Callback {
        /**
         * On current music completed.
         */
        void onCompletion();

        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);
    }

    /**
     * @param callback to be called
     */
    void setCallback(Callback callback);
}