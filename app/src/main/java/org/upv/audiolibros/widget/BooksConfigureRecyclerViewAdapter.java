package org.upv.audiolibros.widget;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.upv.audiolibros.R;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.model.Book;
import java.util.List;

public class BooksConfigureRecyclerViewAdapter extends RecyclerView.Adapter<BooksConfigureRecyclerViewAdapter.ViewHolder> {
    private final ImageLoader imageLoader;
    private final List<Book> books;
    private final View.OnClickListener onBookSelected;

    BooksConfigureRecyclerViewAdapter(ImageLoader imageLoader, BooksDatabase database, View.OnClickListener onBookSelected) {
        this.imageLoader = imageLoader;
        this.books = database.list();
        this.onBookSelected = onBookSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Book book = books.get(position);

        Log.d("COVER_URL", book.getUrlCover());

        imageLoader.get(book.getUrlCover(), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    holder.imgCover.setImageBitmap(bitmap);

                    if(book.getVibrantColor() != -1){
                        holder.itemView.setBackgroundColor(book.getMutedColor());
                        holder.txtTitle.setBackgroundColor(book.getVibrantColor());
                        holder.imgCover.invalidate();
                    }else{
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(@NonNull Palette palette) {
                                holder.itemView.setBackgroundColor(book.getMutedColor());
                                holder.txtTitle.setBackgroundColor(book.getVibrantColor());
                                holder.imgCover.invalidate();
                            }
                        });
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                holder.imgCover.setImageResource(R.drawable.books);
            }
        });

        holder.txtTitle.setText(book.getTitle());

        holder.itemView.setTag(book);
        holder.itemView.setOnClickListener(onBookSelected);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgCover;
        final TextView txtTitle;

        ViewHolder(View view) {
            super(view);
            imgCover = view.findViewById(R.id.book_cover);
            txtTitle = view.findViewById(R.id.book_title);
        }
    }
}
