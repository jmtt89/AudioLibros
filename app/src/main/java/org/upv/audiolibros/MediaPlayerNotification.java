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

import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.service.AudioService;
import org.upv.audiolibros.view.BookDetailActivity;

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
            NotificationChannel channel = new NotificationChannel("Book_Player", channelTitle, NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, "Book_Player");
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder
                .setSmallIcon(R.drawable.ic_stat_media)
                //En algunos dispositivos viejos se muestra esto primero
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //Lo que se ejecuta al clickear sobre la notificacion
                .setContentIntent(PendingIntent.getActivity(context,0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                //Cuando el usuario elimina la notificacion llama esto
                .setDeleteIntent(stopPendingIntent)
                //Se agrega el estilo de Media Player
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setAutoCancel(false);

        if(picture != null){
            builder.setLargeIcon(picture);
        }

        if(isPlaying){
            //Acion de "Pausar"
            builder.addAction( generateAction(context, android.R.drawable.ic_media_pause, context.getString(R.string.btn_stop), "ACTION_STOP" ));
        }else{
            //Acion de "Reanudar"
            builder.addAction( generateAction(context, android.R.drawable.ic_media_play, context.getString(R.string.btn_play), "ACTION_PLAY" ));
        }

        notify(context, builder.build());
    }


    private static NotificationCompat.Action generateAction(Context context, int icon, String title, String intentAction){
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }


    private static void notify(Context context, Notification notification) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    public static void cancel(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }

}
