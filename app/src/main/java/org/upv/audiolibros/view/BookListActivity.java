package org.upv.audiolibros.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.upv.audiolibros.AudioBooks;
import org.upv.audiolibros.database.BooksDatabase;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.R;
import org.upv.audiolibros.view.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;
import static org.upv.audiolibros.controller.Constants.LAST_BOOK_ID;

public class BookListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private SharedPreferences appPreferences;
    private BooksDatabase database;
    private BookRecyclerViewAdapter adapter;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_list);

        database = ((AudioBooks) getApplication()).getDatabase();
        appPreferences =  ((AudioBooks) getApplication()).getAppPreferences();

        setupToolbar();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLastOpen();
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.book_list);
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupToolbar(){

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Navigation Drawe
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Tabs
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText(R.string.lbl_tab_all));
        tabs.addTab(tabs.newTab().setText(R.string.lbl_tab_new));
        tabs.addTab(tabs.newTab().setText(R.string.lbl_tab_read));

        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: //Todos
                        adapter.setFilter(false, false);
                        break;
                    case 1: //Nuevos
                        adapter.setFilter(true, false);
                        break;
                    case 2: //Leidos
                        adapter.setFilter(false, true);
                        break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        AudioBooks app = (AudioBooks) getApplication();
        adapter = new BookRecyclerViewAdapter(app.getImageLoader(), database, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book item = (Book) view.getTag();
                launchDetailView(item.getId());
            }
        });

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager;
        if(mTwoPane){
            manager = new GridLayoutManager(getApplicationContext(), 4);
        } else {
            manager = new GridLayoutManager(getApplicationContext(), 2);
        }

        recyclerView.setLayoutManager(manager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                adapter.setQueryFilter(query);
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                adapter.clearQueryFilter();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_last_read) {
            goLastOpen();
            return true;
        } else if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mensaje de Acerca De");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_epic_genre:
                adapter.setFilter(Book.Genre.EPIC);
                break;
            case R.id.nav_XIX_genre:
                adapter.setFilter(Book.Genre.XIX);
                break;
            case R.id.nav_suspense_genre:
                adapter.setFilter(Book.Genre.SUSPENSE);
            case R.id.nav_all_genres:
            default:
                adapter.setFilter(Book.Genre.ALL);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void goLastOpen() {
        String id = appPreferences.getString(LAST_BOOK_ID, null);
        launchDetailView(id);
    }

    private void launchDetailView(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ARG_BOOK_ID, id);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.book_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
            intent.putExtra(ARG_BOOK_ID, id);
            startActivity(intent);
        }
    }

    public static class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder> {
        private final ImageLoader imageLoader;
        private final BooksDatabase database;
        private final List<Book> books;
        private final List<Book> filteredBooks;
        private final View.OnClickListener onBookSelected;

        private String filterQuery;
        private Book.Genre filterGenre = Book.Genre.ALL;
        private boolean filterNovedad = false;
        private boolean filterLeido = false;

        //region clickListeners
        private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                AlertDialog.Builder menu = new AlertDialog.Builder(view.getContext());
                CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        final Book book = (Book) view.getTag();
                        switch (opcion) {
                            case 0: //Compartir
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_SUBJECT, book.getTitle());
                                intent.putExtra(Intent.EXTRA_TEXT, book.getUrlAudio());
                                view.getContext().startActivity(Intent.createChooser(intent, "Compartir"));
                                break;
                            case 1: //Borrar
                                Snackbar.make(view,"¿Estás seguro?", Snackbar.LENGTH_LONG)
                                        .setAction("SI", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                removeBook(book);
                                            }
                                        })
                                        .show();
                                notifyDataSetChanged();
                                break;
                            case 2: //Insertar
                                addBook(new Book(book));
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        };

        //endregion

        BookRecyclerViewAdapter(ImageLoader imageLoader, BooksDatabase database, View.OnClickListener onBookSelected) {
            this.imageLoader = imageLoader;
            this.database = database;
            this.books = database.list();
            this.filteredBooks = new ArrayList<>(books);
            this.onBookSelected = onBookSelected;
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

        public void addBook(Book book){
            if(!books.contains(book)){
                books.add(0, book);
                database.save(book);
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
                database.save(book);
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
                database.delete(book.getId());
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
            holder.itemView.setOnClickListener(onBookSelected);
            holder.itemView.setOnLongClickListener(mOnLongClickListener);
        }

        @Override
        public int getItemCount() {
            return filteredBooks.size();
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
}
