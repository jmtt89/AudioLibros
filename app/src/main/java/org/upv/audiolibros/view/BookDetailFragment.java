package org.upv.audiolibros.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.upv.audiolibros.AudioBooks;
import org.upv.audiolibros.customViews.OnCambioValorListener;
import org.upv.audiolibros.customViews.ZoomSeekBar;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.R;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.service.Playback;


import java.util.Timer;
import java.util.TimerTask;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;
import static org.upv.audiolibros.controller.Constants.LAST_BOOK_ID;


public class BookDetailFragment extends Fragment
        implements View.OnTouchListener, MediaController.MediaPlayerControl{
    private static final String TAG = "BookDetailFragment";

    private final Timer timer = new Timer();

    private Book book;
    private BooksDatabase database;
    private SharedPreferences appPreferences;

    private MediaController mediaController;

    @Nullable
    private AudioService mService = null;
    private boolean mBound = false;

    private ZoomSeekBar seekBar;

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
            mService = binder.getService();
            mService.setCallback(new Playback.Callback() {
                @Override
                public void onCompletion() {
                    seekBar.setValMin(0);
                    seekBar.setEscalaMin(0);
                    int dur = getDuration()/(60*1000);
                    seekBar.setEscalaMax(dur);
                    seekBar.setValMax(dur);
                    seekBar.setEscalaIni(0);
                    seekBar.setVal(getCurrentPosition());
                    seekBar.setOnCambioValorListener(new OnCambioValorListener() {
                        @Override
                        public void onCambioValor(int nuevoValor) {
                            mService.seekTo(nuevoValor*60*1000);
                        }
                    });
                }

                @Override
                public void onPlaybackStatusChanged(int state) {

                }

                @Override
                public void onError(String error) {

                }
            });
            mBound = true;
            play();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        database = ((AudioBooks)activity.getApplication()).getDatabase();
        appPreferences = ((AudioBooks)activity.getApplication()).getAppPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_BOOK_ID)) {
            String bookId = getArguments().getString(ARG_BOOK_ID);
            book = database.get(bookId);

            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putString(LAST_BOOK_ID, bookId);
            editor.apply();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(book.getTitle());
            }

            mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(getView());
            mediaController.setMediaPlayer(this);
            mediaController.show(0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        TextView titleView = rootView.findViewById(R.id.book_title);
        TextView authorView = rootView.findViewById(R.id.book_author);
        NetworkImageView coverView = rootView.findViewById(R.id.book_cover);

        seekBar = rootView.findViewById(R.id.zoom_seek_bar);

        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor());

        AudioBooks app = (AudioBooks) getActivity().getApplication();
        coverView.setImageUrl(book.getUrlCover(), app.getImageLoader());

        rootView.setOnTouchListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to AudioService
        Intent intent = new Intent(getContext().getApplicationContext(), AudioService.class);
        getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override public void onStop() {
        // Unbind from the service
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override public boolean onTouch(View view, MotionEvent event) {
        view.performClick();
        if(mediaController != null){
            mediaController.show(0);
        }
        return false;
    }

    private void play() {
        mediaController.setEnabled(true);
        if (mService != null) {
            mService.play(book);
            startTimer();
        }
    }

    //region MediaController

    @Override public boolean canPause() {
        return true;
    }

    @Override public boolean canSeekBackward() {
        return true;
    }

    @Override public boolean canSeekForward() {
        return true;
    }

    @Override public int getBufferPercentage() {
        return 0;
    }

    @Override public int getCurrentPosition() {
        try {
            return mService != null ? mService.getCurrentStreamPosition() : 0;
            //return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override public int getDuration() {
        return (int) (mService != null ? mService.getDuration() : 0);
    }

    @Override public boolean isPlaying() {
        return mService != null && mService.isPlaying();
    }

    @Override public void pause() {
        if (mService != null) {
            mService.pause();
            stopTimer();
        }
    }

    @Override public void seekTo(int pos) {
        if (mService != null) {
            mService.seekTo(pos);
        }
    }

    @Override public void start() {
        if (mService != null) {
            mService.start();
        }
    }

    @Override public int getAudioSessionId() {
        return 0;
    }

    //endregion

    protected void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 1000);
    }

    protected void stopTimer(){
        timer.cancel();
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            seekBar.setVal(seekBar.getValMax()+1);
        }
    };

}
