package org.upv.audiolibros.view.detail.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.upv.audiolibros.AudioBooks;
import org.upv.audiolibros.R;
import org.upv.audiolibros.customViews.OnCambioValorListener;
import org.upv.audiolibros.customViews.ZoomSeekBar;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.view.detail.DetailPresenter;
import org.upv.audiolibros.view.detail.DetailPresenterImpl;
import org.upv.audiolibros.view.detail.DetailRepository;
import org.upv.audiolibros.view.detail.DetailRepositoryImpl;
import org.upv.audiolibros.view.detail.PlayerInteractor;
import org.upv.audiolibros.view.detail.PlayerInteractorImpl;
import org.upv.audiolibros.view.detail.StorageInteractor;
import org.upv.audiolibros.view.detail.StorageInteractorImpl;

import java.util.Timer;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;


public class BookDetailFragment extends Fragment
        implements ServiceConnection, View.OnTouchListener, MediaController.MediaPlayerControl, DetailBookView{
    private static final String TAG = "BookDetailFragment";

    private MediaController mediaController;

    private ImageLoader imageLoader;
    private DetailPresenter presenter;

    private View detailWrapper;
    private View progress;
    private CollapsingToolbarLayout appBarLayout;
    private ZoomSeekBar seekBar;
    private TextView titleView;
    private TextView authorView;
    private NetworkImageView coverView;



    private boolean mBound = false;


    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        AudioService.LocalBinder binder = (AudioService.LocalBinder) service;
        presenter.attach(binder.getService());
        mBound = true;
        mediaController.setEnabled(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        presenter.detach();
        mBound = false;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    public void injectDependencies(){
        AudioBooks app = (AudioBooks) getActivity().getApplication();
        mediaController = new MediaController(getActivity());
        imageLoader = app.getImageLoader();
        EventBus eventBus = app.getEventBus();
        PlayerInteractor player = new PlayerInteractorImpl(new Timer(), eventBus);
        DetailRepository repository = new DetailRepositoryImpl(app.getBooksDatabase(), eventBus);
        StorageInteractor storage = new StorageInteractorImpl(repository);
        presenter = new DetailPresenterImpl(this, eventBus, player, storage);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        bindView(rootView);
        rootView.setOnTouchListener(this);
        presenter.onCreate();

        if (getArguments() != null && getArguments().containsKey(ARG_BOOK_ID)) {
            String bookId = getArguments().getString(ARG_BOOK_ID);
            presenter.loadBook(bookId);
        }

        return rootView;
    }

    private void bindView(View rootView){
        Activity activity = this.getActivity();
        if (activity != null) {
            appBarLayout = activity.findViewById(R.id.toolbar_layout);
        }

        detailWrapper = rootView.findViewById(R.id.detail_wrapper);
        progress = rootView.findViewById(R.id.loading);

        titleView = rootView.findViewById(R.id.book_title);
        authorView = rootView.findViewById(R.id.book_author);
        coverView = rootView.findViewById(R.id.book_cover);

        seekBar = rootView.findViewById(R.id.zoom_seek_bar);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to AudioService
        Intent intent = new Intent(getContext().getApplicationContext(), AudioService.class);
        getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        // Unbind from the service
        if (mBound) {
            getContext().unbindService(this);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    private void play(Book book) {
        presenter.streamPlay(book);
    }

    //region MediaController

    @Override public boolean canPause() {
        return presenter.streamCanPause();
    }

    @Override public boolean canSeekBackward() {
        return presenter.streamCanSeekBackward();
    }

    @Override public boolean canSeekForward() {
        return presenter.streamCanSeekForward();
    }

    @Override public int getBufferPercentage() {
        return presenter.streamBufferPercentage();
    }

    @Override public int getCurrentPosition() {
        return presenter.streamCurrentPosition();
    }

    @Override public int getDuration() {
        return (int) presenter.streamDuration();
    }

    @Override public boolean isPlaying() {
        return presenter.streamIsPlaying();
    }

    @Override public void pause() {
        presenter.streamPause();
    }

    @Override public void seekTo(int pos) {
        presenter.streamSeek(pos);
    }

    @Override public void start() {
        presenter.streamStart();
    }

    @Override public int getAudioSessionId() {
        return presenter.streamAudioSessionId();
    }

    //endregion

    @Override
    public void onSeekbarProgressUpdate(){
        seekBar.setVal(seekBar.getValMax()+1);
    }

    @Override
    public void enableInputs() {
        setInputs(true);
    }

    @Override
    public void disableInputs() {
        setInputs(false);
    }

    private void setInputs(boolean enable) {
        //mediaController.setEnabled(enable);
    }



    @Override
    public void showProgressbar() {
        showProgressbar(true);
    }

    @Override
    public void hideProgressbar() {
        showProgressbar(false);
    }


    private void showProgressbar(boolean enable) {
        if (progress != null) {
            progress.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
        detailWrapper.setVisibility(enable ? View.GONE : View.VISIBLE);
    }



    @Override
    public void fillBookData(Book book) {
        if (appBarLayout != null) {
            appBarLayout.setTitle(book.getTitle());
        }

        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor());
        coverView.setImageUrl(book.getUrlCover(), imageLoader);

        presenter.streamLoad(book);
    }




    @Override
    public boolean onTouch(View view, MotionEvent event) {
        presenter.showMediaController();
        //view.performClick();
        return false;
    }

    @Override
    public void showMediaController() {
        if(mediaController != null){
            mediaController.show();
        }
    }




    @Override
    public void onStreamReady(){
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
                presenter.streamSeek(nuevoValor*60*1000);
            }
        });

        mediaController.setAnchorView(getView());
        mediaController.setMediaPlayer(this);
        mediaController.show();
    }



    @Override
    public void showError(String error) {
        Snackbar.make(detailWrapper, error, Snackbar.LENGTH_LONG).show();
    }
}
