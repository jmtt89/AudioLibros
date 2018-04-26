package org.upv.audiolibros.view.list.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.upv.audiolibros.AudioBooks;
import org.upv.audiolibros.R;
import org.upv.audiolibros.libs.EventBus;
import org.upv.audiolibros.model.Book;
import org.upv.audiolibros.view.list.ListBooksPresenter;
import org.upv.audiolibros.view.list.ListBooksPresenterImpl;
import org.upv.audiolibros.view.list.ListBooksRepository;
import org.upv.audiolibros.view.list.ListBooksRepositoryImpl;
import org.upv.audiolibros.view.list.StorageInteractor;
import org.upv.audiolibros.view.list.StorageInteractorImpl;
import org.upv.audiolibros.view.list.ui.clickEvents.ExtraOptionsClickAction;
import org.upv.audiolibros.view.list.ui.clickEvents.OpenDetailClickAction;
import org.upv.audiolibros.view.detail.ui.BookDetailActivity;
import org.upv.audiolibros.view.detail.ui.BookDetailFragment;
import org.upv.audiolibros.view.list.ui.adapter.BookRecyclerViewAdapter;
import org.upv.audiolibros.view.settings.SettingsActivity;

import java.util.List;

import static org.upv.audiolibros.controller.Constants.ARG_BOOK_ID;

public class BookListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ListBooksView{
    private BookRecyclerViewAdapter adapter;
    private ListBooksPresenter presenter;
    private boolean mTwoPane;

    private View container;
    private Toolbar toolbar;
    private TabLayout tabs;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    @Nullable private ProgressBar progress;
    private RecyclerView bookListRecyclerView;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();

        bindView();
        presenter.onCreate();
        initializeView();
    }

    private void injectDependencies() {
        AudioBooks app = (AudioBooks) getApplication();
        EventBus eventBus = app.getEventBus();
        ListBooksRepository repository = new ListBooksRepositoryImpl(this, app.getBooksDatabase(), eventBus);
        StorageInteractor interactor = new StorageInteractorImpl(repository);
        presenter = new ListBooksPresenterImpl(this, eventBus, interactor);
    }

    private void bindView(){
        setContentView(R.layout.main_activity_list);
        container = findViewById(R.id.book_detail_container);
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        progress = findViewById(R.id.loading);
        bookListRecyclerView = findViewById(R.id.book_list);

        fab = findViewById(R.id.fab);
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
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }


    private void initializeView(){
        setupToolbar();
        if (container != null) {
            mTwoPane = true;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.loadLastBook();
            }
        });
        setupBooksList();
    }

    private void setupToolbar(){

        //Toolbar

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Navigation Drawe
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);


        // Tabs
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

    private void setupBooksList() {
        AudioBooks app = (AudioBooks) getApplication();
        adapter = new BookRecyclerViewAdapter(app.getImageLoader());

        adapter.setClickAction(new OpenDetailClickAction(this));
        adapter.setLongClickAction(new ExtraOptionsClickAction(getApplicationContext(), adapter));

        bookListRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager manager;
        if(mTwoPane){
            manager = new GridLayoutManager(getApplicationContext(), 4);
        } else {
            manager = new GridLayoutManager(getApplicationContext(), 2);
        }

        bookListRecyclerView.setLayoutManager(manager);
        presenter.loadBooks();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchObservable searchObservable = new SearchObservable();
        searchObservable.addObserver(adapter);
        searchView.setOnQueryTextListener(searchObservable);

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
        if (id == R.id.action_last_read) {
            presenter.loadLastBook();
            return true;
        } else if (/*id == R.id.action_settings*/ false) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        //Esta vista no tiene inputs por ahora
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
        bookListRecyclerView.setVisibility(enable ? View.GONE : View.VISIBLE);
    }



    @Override
    public void showBookDetail(Book book) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ARG_BOOK_ID, book.getId());
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.book_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
            intent.putExtra(ARG_BOOK_ID, book.getId());
            startActivity(intent);
        }
    }


    @Override
    public void addCollection(List<Book> books) {
        adapter.addBooks(books);
    }

    @Override
    public void addBook(Book book) {
        adapter.addBook(book);
    }

    @Override
    public void updateBook(Book book) {
        adapter.updateBook(book);
    }

    @Override
    public void deleteBook() {
        adapter.removeBook(null);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(container, error, Snackbar.LENGTH_LONG).show();
    }
}
