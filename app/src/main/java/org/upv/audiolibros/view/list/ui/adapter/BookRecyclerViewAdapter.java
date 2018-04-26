package org.upv.audiolibros.view.list.ui.adapter;

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
import org.upv.audiolibros.controller.BooksController;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.view.list.ui.clickEvents.ClickAction;
import org.upv.audiolibros.view.list.ui.clickEvents.EmptyClickAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> implements Observer {
    private final ImageLoader imageLoader;

    private final List<Book> books;
    private final List<Book> filteredBooks;

    private ClickAction clickAction = new EmptyClickAction();
    private ClickAction longClickAction = new EmptyClickAction();

    private String filterQuery;
    private Book.Genre filterGenre = Book.Genre.ALL;
    private boolean filterNovedad = false;
    private boolean filterLeido = false;

    //region clickListeners

    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }

    public void setLongClickAction(ClickAction longClickAction){
        this.longClickAction = longClickAction;
    }

    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View view) {

            return true;
        }
    };

    //endregion

    public BookRecyclerViewAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        this.books = new ArrayList<>();
        this.filteredBooks = new ArrayList<>();
    }



    public void setFilter(boolean novedad, boolean leido) {
        filterNovedad = novedad;
        filterLeido = leido;
        resetFilter();
        notifyDataSetChanged();
    }

    public void setFilter(Book.Genre genre) {
        filterGenre = genre;
        resetFilter();
        notifyDataSetChanged();
    }

    public void setQueryFilter(String query) {
        filterQuery = query;
        resetFilter();
        notifyDataSetChanged();
    }

    public void clearQueryFilter() {
        filterQuery = null;
        resetFilter();
        notifyDataSetChanged();
    }

    private void resetFilter() {
        filteredBooks.clear();
        for (Book book : books) {
            if(
                    (filterQuery == null || book.getTitle().toLowerCase().contains(filterQuery.toLowerCase()))
                            && (filterGenre.equals(Book.Genre.ALL) || book.getGenre().equals(filterGenre))
                            && ((!filterNovedad || book.getNovedad()) && (!filterLeido || book.getLeido()))){
                filteredBooks.add(book);
            }
        }
    }



    public void addBooks(Collection<Book> books) {
        for (Book book: books) {
            if(!this.books.contains(book)) {
                this.books.add(book);
            }
        }
        resetFilter();
    }

    public void addBook(Book book){
        if(!books.contains(book)){
            books.add(0, book);
            resetFilter();
            int idx = filteredBooks.indexOf(book);
            if(idx >= 0){
                notifyItemInserted(idx);
            }
        }
    }

    public void updateBook(Book book) {
        int idx = books.indexOf(book);
        if(idx >= 0){
            books.set(idx, book);
            idx = filteredBooks.indexOf(book);
            if(idx >= 0){
                filteredBooks.set(idx, book);
                notifyItemChanged(idx);
            }
        }else{
            addBook(book);
        }
    }

    public void removeBook(Book book){
        if(books.contains(book)){
            books.remove(book);
            int idx = filteredBooks.indexOf(book);
            if(idx >= 0){
                filteredBooks.remove(book);
                notifyItemRemoved(idx);
            }
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Book book = filteredBooks.get(position);

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
                                book.setMutedColor(palette.getLightMutedColor(0));
                                book.setVibrantColor(palette.getLightVibrantColor(0));
                                updateBook(book);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAction.execute(book);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickAction.execute(book);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredBooks.size();
    }

    @Override
    public void update(Observable o, Object arg) {
        setQueryFilter((String) arg);
        notifyDataSetChanged();
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