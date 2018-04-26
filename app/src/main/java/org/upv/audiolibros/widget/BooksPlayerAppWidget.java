package org.upv.audiolibros.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.upv.audiolibros.R;
import org.upv.audiolibros.controller.BooksController;
import org.upv.audiolibros.controller.NetworkController;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.view.list.ui.BookListActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BooksPlayerAppWidgetConfigureActivity BooksPlayerAppWidgetConfigureActivity}
 */
public class BooksPlayerAppWidget extends AppWidgetProvider {
    private static final String OnClickPlay  = "PlayAction";
    private static final String OnClickLaunch = "LaunchAction";
    private static final String TAG = "Widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            //BooksPlayerAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null && intent.getAction() != null){
            switch (intent.getAction()){
                case OnClickLaunch:
                    Log.d(TAG, "onClick: LAUNCH");
                    break;
                case OnClickPlay:
                    Log.d(TAG, "onClick: PLAY");
                    break;
                default:
                    Log.w(TAG, "onReceive: Unknown Action: "+ intent.getAction());
                    break;
            }
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Context appContext = context.getApplicationContext();
        SharedPreferences pref = appContext.getSharedPreferences("WIDGET_SELECTOR", MODE_PRIVATE);
        BooksController controller = BooksController.getInstance(appContext);

        // Construct the RemoteViews object
        RemoteViews rootView = new RemoteViews(context.getPackageName(), R.layout.books_player_app_widget);

        boolean updated = false;
        String bookId = pref.getString(appWidgetId+"", null);
        if(bookId != null){
            Book lastBook = controller.get(bookId);
            if(lastBook != null){
                updated = true;
                addCoverImage(rootView, lastBook);
                rootView.setTextViewText(R.id.book_title, lastBook.getTitle());
                rootView.setTextViewText(R.id.book_author, lastBook.getAuthor());
                rootView.setInt(R.id.widget_wrapper, "setBackgroundColor", lastBook.getMutedColor());
            }
        }

        if(!updated){
            rootView.setTextViewText(R.id.book_title, "No Book Played");
            rootView.setTextViewText(R.id.book_author, "Audio Book Player");
            rootView.setBoolean(R.id.action_play, "setEnable", false);
            rootView.setInt(R.id.widget_wrapper, "setBackgroundColor", Color.WHITE);
        }

        //Callbacks
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction("ACTION_TOGGLE");
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        rootView.setOnClickPendingIntent(R.id.action_play, pendingIntent);

        //rootView.setOnClickPendingIntent(R.id.action_play, getPendingSelfIntent(context, OnClickPlay));

        Intent start = new Intent(context, BookListActivity.class);
        PendingIntent startPending = PendingIntent.getService(context, 1, start, 0);
        rootView.setOnClickPendingIntent(R.id.action_launch, startPending);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rootView);
    }

    private static void addCoverImage(final RemoteViews view, final Book book) {
        NetworkController
                .getInstance()
                .getImageLoader()
                .get(book.getUrlCover(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        Bitmap bitmap = response.getBitmap();
                        if (bitmap != null) {
                            view.setImageViewBitmap(R.id.book_cover, bitmap);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        view.setImageViewResource(R.id.book_cover, R.drawable.books);
                    }
                });
    }

    private static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, BooksPlayerAppWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

