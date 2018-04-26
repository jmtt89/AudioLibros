package org.upv.audiolibros;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.upv.audiolibros.controller.NetworkController;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.view.detail.ui.BookDetailActivity;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;


public class MediaPlayerNotification {
    private static final String NOTIFICATION_TAG = "MediaPlayer";

    public static void notify(Context context, Book book, boolean isPlaying) {
        final Resources res = context.getResources();

        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.books);
        final String ticker = book.getTitle();
        final String title = book.getTitle();
        final String text = book.getAuthor();

        Intent intent = new Intent(context, AudioService.class);
        intent.setAction("ACTION_DELETE");
        PendingIntent stopPendingIntent = PendingIntent.getService(context, 1, intent, 0);

        Intent startIntent = new Intent(context, BookDetailActivity.class);
        startIntent.putExtra(ARG_BOOK_ID, book.getId());

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelTitle = context.getString(R.string.notification_channel_title);
            NotificationChannel channel = new NotificationChannel("Book_Player", channelTitle, NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, "Book_Player");
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        // Construct the RemoteViews object
        RemoteViews rootView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);

        addCoverImage(rootView, book);
        rootView.setTextViewText(R.id.book_title, book.getTitle());
        rootView.setTextViewText(R.id.book_author, book.getAuthor());
        rootView.setInt(R.id.notification_wrapper, "setBackgroundColor", book.getMutedColor());
        if(isPlaying){
            //Acion de "Pausar"
            rootView = generateAction(context, rootView, android.R.drawable.ic_media_pause, context.getString(R.string.btn_stop), "ACTION_STOP" );
        }else{
            //Acion de "Reanudar"
            rootView = generateAction(context, rootView, android.R.drawable.ic_media_play, context.getString(R.string.btn_play), "ACTION_PLAY" );
        }

        builder
                .setSmallIcon(R.drawable.ic_stat_media)
                //En algunos dispositivos viejos se muestra esto primero
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //Lo que se ejecuta al clickear sobre la notificacion
                .setContentIntent(PendingIntent.getActivity(context,0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                //Cuando el usuario elimina la notificacion llama esto
                .setDeleteIntent(stopPendingIntent)
                .setCustomBigContentView(rootView)
                .setAutoCancel(false);

        notify(context, builder.build());
    }


    private static RemoteViews generateAction(Context context, RemoteViews rootView, int icon, String title, String intentAction){
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        rootView.setOnClickPendingIntent(R.id.btn_action_play, pendingIntent);
        //rootView.setTextViewCompoundDrawables(R.id.btn_action_play,0, icon, 0, 0);
        //rootView.setString(R.id.btn_action_play, "setText", title);
        return rootView;
    }


    private static void notify(Context context, Notification notification) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    public static void cancel(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
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

}
